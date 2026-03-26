package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.model.Friendship;
import com.zalo.model.User;
import com.zalo.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService service;

    // 📤 gửi lời mời
    @PostMapping("/request/{toId}")
    public void send(@RequestParam Long userId,
                     @PathVariable Long toId) {
        service.sendRequest(userId, toId);
    }

    // ✅ accept
    @PostMapping("/accept/{otherId}")
    public void accept(@RequestParam Long userId,
                       @PathVariable Long otherId) {
        service.accept(userId, otherId);
    }

    // ❌ reject
    @PostMapping("/reject/{otherId}")
    public void reject(@RequestParam Long userId,
                       @PathVariable Long otherId) {
        service.reject(userId, otherId);
    }

    // 🗑️ cancel
    @DeleteMapping("/cancel/{otherId}")
    public void cancel(@RequestParam Long userId,
                       @PathVariable Long otherId) {
        service.cancelRequest(userId, otherId);
    }

    // 💔 unfriend
    @DeleteMapping("/unfriend/{otherId}")
    public void unfriend(@RequestParam Long userId,
                         @PathVariable Long otherId) {
        service.unfriend(userId, otherId);
    }

    // 📋 list bạn
    @GetMapping
    public List<User> getFriends(@RequestParam Long userId) {
        return service.getFriends(userId);
    }

    // 📥 request nhận
    @GetMapping("/received")
    public List<Friendship> received(@RequestParam Long userId) {
        return service.getReceived(userId);
    }

    // 📤 request gửi
    @GetMapping("/sent")
    public List<Friendship> sent(@RequestParam Long userId) {
        return service.getSent(userId);
    }

    @GetMapping("/is-friend/{otherId}")
    public boolean isFriend(@CurrentUser User user, @PathVariable Long otherId) {
        return service.isFriend(user.getId(), otherId);
    }
}
