package com.zalo.service;

import com.zalo.dto.request.Friendship.CreateFriendship;
import com.zalo.model.Friendship;
import com.zalo.model.User;
import com.zalo.model.enums.FriendStatus;
import com.zalo.repository.FriendshipRepository;
import com.zalo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository repo;
    private final UserRepository userRepo;

    private Long min(Long a, Long b) {
        return Math.min(a, b);
    }

    private Long max(Long a, Long b) {
        return Math.max(a, b);
    }

    private Friendship getRelation(Long u1, Long u2) {
        return repo.findByUser1IdAndUser2Id(min(u1, u2), max(u1, u2))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    // 📤 gửi lời mời
    public void sendRequest(Long fromId, CreateFriendship dto) {
        Long u1 = min(fromId, dto.toId);
        Long u2 = max(fromId, dto.toId);

        if (repo.findByUser1IdAndUser2Id(u1, u2).isPresent()) {
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
    }

    // ❌ reject
    public void reject(Long userId, Long otherId) {
        Friendship f = getRelation(userId, otherId);

        if (!f.getStatus().equals(FriendStatus.PENDING)) {
            throw new RuntimeException("Invalid state");
        }

        f.setStatus(FriendStatus.REJECTED);
        repo.save(f);
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
    public List<User> getFriends(Long userId) {
        return repo.findFriends(userId).stream()
                .map(f -> f.getUser1().getId().equals(userId)
                        ? f.getUser2()
                        : f.getUser1())
                .toList();
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
        Long u1 = Math.min(a, b);
        Long u2 = Math.max(a, b);

        return repo.getFriend(u1, u2);
    }
}
