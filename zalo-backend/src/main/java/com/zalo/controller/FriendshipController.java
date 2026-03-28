package com.zalo.controller;

import com.zalo.configuration.anotation.CurrentUser;
import com.zalo.dto.request.Friendship.CreateFriendship;
import com.zalo.dto.response.Friendship.FriendshipResponse;
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
    @PostMapping("/request")
    public void send(@CurrentUser User user,
                     @RequestBody CreateFriendship dto) {
        service.sendRequest(user.getId(), dto);
    }

    // ✅ accept
    @PostMapping("/accept/{otherId}")
    public void accept(@CurrentUser User user,
                       @PathVariable Long otherId) {
        service.accept(user.getId(), otherId);
    }

    // ❌ reject
    @PostMapping("/reject/{otherId}")
    public void reject(@CurrentUser User user,
                       @PathVariable Long otherId) {
        service.reject(user.getId(), otherId);
    }

    // 🗑️ cancel
    @DeleteMapping("/cancel/{otherId}")
    public void cancel(@CurrentUser User user,
                       @PathVariable Long otherId) {
        service.cancelRequest(user.getId(), otherId);
    }

    // 💔 unfriend
    @DeleteMapping("/unfriend/{otherId}")
    public void unfriend(@CurrentUser User user,
                         @PathVariable Long otherId) {
        service.unfriend(user.getId(), otherId);
    }

    // 📋 list bạn
    @GetMapping
    public List<User> getFriends(@CurrentUser User user) {
        return service.getFriends(user.getId());
    }

    // 📥 request nhận
    @GetMapping("/received")
    public List<FriendshipResponse> received(@CurrentUser User user) {
        return service.getReceived(user.getId()).stream().map(e -> new FriendshipResponse(e, "user1", "user2")).toList();
    }

    // 📤 request gửi
    @GetMapping("/sent")
    public List<FriendshipResponse> sent(@CurrentUser User user) {
        return service.getSent(user.getId()).stream().map(e -> new FriendshipResponse(e, "user1", "user2")).toList();
    }

    @GetMapping("/friend/{otherId}")
    public FriendshipResponse getFriend(@CurrentUser User user, @PathVariable Long otherId) {
        Friendship f = service.getFriend(user.getId(), otherId);

        if(f == null){
         return null;
        }
        return new FriendshipResponse(service.getFriend(user.getId(), otherId));
    }
}
