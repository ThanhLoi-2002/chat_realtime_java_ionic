package com.zalo.service;

import com.zalo.dto.request.Friendship.CreateFriendship;
import com.zalo.dto.request.Message.CreateMessageRequest;
import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.Conversation;
import com.zalo.model.Friendship;
import com.zalo.model.Message;
import com.zalo.model.User;
import com.zalo.model.enums.ConversationType;
import com.zalo.model.enums.FriendStatus;
import com.zalo.model.enums.MessageType;
import com.zalo.repository.ConversationRepository;
import com.zalo.repository.FriendshipRepository;
import com.zalo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository repo;
    private final UserRepository userRepo;
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;

    private Long min(Long a, Long b) {
        return Math.min(a, b);
    }

    private Long max(Long a, Long b) {
        return Math.max(a, b);
    }

    private Friendship getRelation(Long u1, Long u2) {
        return repo.findActiveFriendship(min(u1, u2), max(u1, u2)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    // 📤 gửi lời mời
    public Friendship sendRequest(Long fromId, CreateFriendship dto) {
        Long u1 = min(fromId, dto.toId);
        Long u2 = max(fromId, dto.toId);

        if (repo.findActiveFriendship(u1, u2).isPresent()) {
            throw new RuntimeException("Already exists");
        }

        Friendship f = new Friendship();
        f.setUser1(userRepo.getReferenceById(u1));
        f.setUser2(userRepo.getReferenceById(u2));
        f.setContent(dto.content);
        f.setStatus(FriendStatus.PENDING);
        f.setActionUserId(fromId);
        f.setCu(fromId);

        repo.save(f);

        return f;
    }

    // ✅ accept
    public void accept(Long userId, Long otherId) {
        Friendship f = getRelation(userId, otherId);

        if (!f.getStatus().equals(FriendStatus.PENDING)) {
            throw new RuntimeException("Invalid state");
        }

        if (f.getActionUserId().equals(userId)) {
            throw new RuntimeException("Cannot accept your own request");
        }

        f.setStatus(FriendStatus.ACCEPTED);
        repo.save(f);

        // create system message
        CreateMessageRequest dto = new CreateMessageRequest();
        dto.content = "youTwoBecomeFriends";
        dto.contentType = MessageType.SYSTEM;

        Optional<Conversation> c = conversationRepository.findPrivateBetween(userId, otherId, ConversationType.PRIVATE.name());

        if(c.isEmpty()){
            c = Optional.ofNullable(conversationService.createPrivateConversation(userId, otherId));
        }

        c.ifPresent(conversation -> messageService.createSystemMessage(conversation.getId(), dto));

    }

    // ❌ reject
    public void reject(Long userId, Long otherId) {
        Friendship f = getRelation(userId, otherId);

        if (!f.getStatus().equals(FriendStatus.PENDING)) {
            throw new RuntimeException("Invalid state");
        }

        repo.delete(f);
    }

    // 🗑️ hủy lời mời
    public void cancelRequest(Long userId, Long otherId) {
        Friendship f = getRelation(userId, otherId);

        if (!f.getActionUserId().equals(userId)) {
            throw new RuntimeException("Not your request");
        }

        repo.delete(f);
    }

    // 💔 unfriend
    public void unfriend(Long userId, Long otherId) {
        Friendship f = getRelation(userId, otherId);

        if (!f.getStatus().equals(FriendStatus.ACCEPTED)) {
            throw new RuntimeException("Not friends");
        }

        repo.delete(f);
    }

    // 📋 danh sách bạn
    public List<UserResponse> getFriends(Long userId) {
        return repo.findFriends(userId).stream().map(f -> f.getUser1().getId().equals(userId) ? new UserResponse(f.getUser2()) : new UserResponse(f.getUser1())).toList();
    }

    // 📥 lời mời nhận
    public List<Friendship> getReceived(Long userId) {
        return repo.findReceivedRequests(userId);
    }

    // 📤 đã gửi
    public List<Friendship> getSent(Long userId) {
        return repo.findSentRequests(userId);
    }

    public Friendship getFriend(Long a, Long b) {
        Long u1 = min(a, b);
        Long u2 = max(a, b);

        return repo.getFriend(u1, u2);
    }
}
