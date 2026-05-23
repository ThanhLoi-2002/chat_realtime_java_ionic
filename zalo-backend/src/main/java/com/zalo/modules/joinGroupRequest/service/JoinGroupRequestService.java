package com.zalo.modules.joinGroupRequest.service;

import com.zalo.common.configuration.json.G;
import com.zalo.common.service.WebsocketService;
import com.zalo.modules.conversation.entities.Conversation;
import com.zalo.modules.conversation.entities.ConversationMember;
import com.zalo.modules.conversation.entities.MemberRole;
import com.zalo.modules.conversation.entities.MemberStatus;
import com.zalo.modules.conversation.service.ConversationMemberRepository;
import com.zalo.modules.conversation.service.ConversationService;
import com.zalo.modules.conversation.service.MemberService;
import com.zalo.modules.joinGroupRequest.dto.request.JoinGroupDto;
import com.zalo.modules.joinGroupRequest.dto.response.JoinGroupRequestResponse;
import com.zalo.modules.joinGroupRequest.entity.JoinGroupRequest;
import com.zalo.modules.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.message.entity.SystemMessageType;
import com.zalo.modules.message.service.SystemMessageInterface;
import com.zalo.modules.user.dto.response.UserResponse;
import com.zalo.modules.user.entities.User;
import com.zalo.modules.user.service.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JoinGroupRequestService {
    JoinGroupRequestRepository joinGroupRequestRepo;
    ConversationService convService;
    WebsocketService websocketService;
    ConversationMemberRepository memberRepo;
    SystemMessageInterface systemMessageInterface;
    MemberService memberService;
    UserRepository userRepo;

    public void requestToJoinGroup(Long userId, JoinGroupDto dto) {
        Conversation conv = convService.findById(dto.convId);

        if (conv.getSettings() != null && conv.getSettings().isMembershipApproval()) {
            JoinGroupRequest e = new JoinGroupRequest();
            e.setConversationId(dto.getConvId());
            e.setMessage(dto.getMessage());
            e.setCu(userId);

            joinGroupRequestRepo.save(e);

            User u = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

            JoinGroupRequestResponse rp = new JoinGroupRequestResponse(e);
            rp.createdBy = new UserResponse(u);
            websocketService.newJoinGroupRequest(rp);
        } else {
            addMemberToGroup(dto.getConvId(), userId);
        }
    }

    public void addMemberToGroup(Long conversationId, Long userId) {
        ConversationMember m = new ConversationMember();
        m.setConversationId(conversationId);
        m.setUserId(userId);
        m.setRole(MemberRole.MEMBER);
        m.setStatus(MemberStatus.ACTIVE);

        memberRepo.save(m);

        CreateSystemMessageRequest dto = new CreateSystemMessageRequest();
        dto.conversationId = conversationId;
        dto.senderId = userId;
        dto.systemMessageType = SystemMessageType.JOIN_GROUP;
        dto.content = "recentJoinedToGroup";

        websocketService.addMembers(conversationId, memberService.getMembers(conversationId));
        systemMessageInterface.createSystemMessage(dto);
    }

    public List<JoinGroupRequest> getListByConvId(Long convId) {
        return joinGroupRequestRepo.findByConversationId(convId);
    }
}
