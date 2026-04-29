package com.zalo.common.service;

import com.zalo.common.configuration.json.G;
import com.zalo.modules.conversation.dto.IsMentionDto;
import com.zalo.modules.conversation.dto.respone.MemberResponse;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.service.ConversationInterface;
import com.zalo.modules.message.dto.response.MessageReactionResponse;
import com.zalo.modules.message.dto.response.MessageResponse;
import com.zalo.modules.conversation.dto.respone.ConversationResponse;
import com.zalo.common.gateway.UserOnlineStorage;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageReaction;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.conversation.service.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WebsocketService {
    private final ConversationMemberRepository memberRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserOnlineStorage userOnlineStorage;
    public final ConversationRepository conversationRepository;

    public void sendMessage(MessageResponse message, ConversationResponse conv, List<ConversationMember> members) {
        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            if (sessions != null && !sessions.isEmpty()) {
                Long countUnread = conversationRepository.countUnread(conv.getId(), member.getUserId());
                List<IsMentionDto> mentions = conversationRepository.checkMentionsInUnread(Collections.singletonList(conv.getId()),member.getUserId());
                Map<Long, Integer> mapIsMention = mentions.stream().collect(Collectors.toMap(
                        IsMentionDto::getConversationId,  // key
                        IsMentionDto::getIsMention            // value
                ));

                conv.setUnread(countUnread);
                conv.setIsMention(mapIsMention.getOrDefault(conv.getId(), 0) == 1);

                Map<String, Object> payload = new HashMap<>();
                payload.put("message", message);
                payload.put("conversation", conv);
                for (String sessionId : sessions) {
                    messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.newMessages", payload, userOnlineStorage.createHeaders(sessionId));
                }
            }
        }
    }

    public void updateMessage(Message message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", new MessageResponse(message, "sender", "replyToMessage"));

        realtimeToConversation(message.getConversationId(), payload, "/queue/chat.updateMessages");
    }

    public void addReaction(Long conversationId, MessageReaction mr) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reaction", new MessageReactionResponse(mr, "createdBy"));

        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".addReaction");
    }

    public void removeAllReactionByUserId(Long conversationId, Long messageId, List<MessageReaction> mrs) {
        List<MessageReactionResponse> list = mrs.stream().map(m -> new MessageReactionResponse(m, "createdBy")).toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("reactions", list);
        payload.put("messageId", messageId);

        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".reactions");
    }

    public void leaveGroup(Long conversationId, Long userId){
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);

        //Realtime for members in the group
        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".leaveGroup");

        //Realtime for user who leaved the group
        Set<String> sessions = userOnlineStorage.getSessions(userId);
        for (String sessionId : sessions) {
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.conversation." + conversationId + ".leaveGroup", payload, userOnlineStorage.createHeaders(sessionId));
        }
    }

    public void kickMember(Long conversationId, Long memberId){
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", memberId);
        payload.put("conversationId", conversationId);

        //Realtime for members in the group
        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".kickMember");

        //Realtime for user who leaved the group
        Set<String> sessions = userOnlineStorage.getSessions(memberId);
        for (String sessionId : sessions) {
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/chat.conversation.kickMember." + memberId, payload, userOnlineStorage.createHeaders(sessionId));
        }
    }

    public void addMembers(Long conversationId, List<MemberResponse> memberResponses){
        Map<String, Object> payload = new HashMap<>();
        payload.put("members", memberResponses);
        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".addMembers");
    }

    public void disbandGroup(Long conversationId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("conversationId", conversationId);

        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".disbandGroup");
    }

    public void updateMemberList(Long conversationId, List<MemberResponse> members) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("members", members);

        realtimeToConversation(conversationId, payload, "/queue/chat.conversation." + conversationId + ".updateMemberList");
    }

    public void realtimeToConversation(Long conversationId, Object payload, String destination){
        List<ConversationMember> members = memberRepo.findByConversationId(conversationId);

        for (ConversationMember member : members) {
            Set<String> sessions = userOnlineStorage.getSessions(member.getUserId());

            if (sessions != null && !sessions.isEmpty()) {
                for (String sessionId : sessions) {

                    messagingTemplate.convertAndSendToUser(sessionId, destination, payload, userOnlineStorage.createHeaders(sessionId));
                }
            }
        }
    }

    public void broadcastStatus(Long userId, boolean online) {
        if(online){
            System.out.println("Send online truetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetruetrue");
        }else{
            System.out.println("Send online falsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalse");
        }
        messagingTemplate.convertAndSend("/topic/user.status." + userId, (Object) Map.of("userId", userId, "online", online));
    }
}
