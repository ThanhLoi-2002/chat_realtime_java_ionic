package com.zalo.modules.app.friendship;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.app.friendship.dto.request.CreateFriendship;
import com.zalo.modules.app.friendship.dto.response.FriendshipResponse;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import com.zalo.modules.admin.system.user.dto.response.UserResponse;
import com.zalo.modules.app.friendship.entity.Friendship;
import com.zalo.modules.app.friendship.service.FriendshipService;
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
    public FriendshipResponse send(@CurrentUser UserPayload user, @RequestBody CreateFriendship dto) {
        return new FriendshipResponse(service.sendRequest(user.getId(), dto), "user1", "user2");
    }

    // ✅ accept
    @PostMapping("/accept/{otherId}")
    public void accept(@CurrentUser UserPayload user, @PathVariable Long otherId) {
        service.accept(user.getId(), otherId);
    }

    // ❌ reject
    @PostMapping("/reject/{otherId}")
    public void reject(@CurrentUser UserPayload user, @PathVariable Long otherId) {
        service.reject(user.getId(), otherId);
    }

    // 🗑️ cancel
    @DeleteMapping("/cancel/{otherId}")
    public void cancel(@CurrentUser UserPayload user, @PathVariable Long otherId) {
        service.cancelRequest(user.getId(), otherId);
    }

    // 💔 unfriend
    @DeleteMapping("/unfriend/{otherId}")
    public void unfriend(@CurrentUser UserPayload user, @PathVariable Long otherId) {
        service.unfriend(user.getId(), otherId);
    }

    // 📋 list bạn
    @GetMapping
    public List<UserResponse> getFriends(@CurrentUser UserPayload user) {
        return service.getFriends(user.getId());
    }

    // 📥 request nhận
    @GetMapping("/received")
    public List<FriendshipResponse> received(@CurrentUser UserPayload user) {
        return service.getReceived(user.getId()).stream().map(e -> new FriendshipResponse(e, "user1", "user2")).toList();
    }

    // 📤 request gửi
    @GetMapping("/sent")
    public List<FriendshipResponse> sent(@CurrentUser UserPayload user) {
        return service.getSent(user.getId()).stream().map(e -> new FriendshipResponse(e, "user1", "user2")).toList();
    }

    @GetMapping("/friend/{otherId}")
    public FriendshipResponse getFriend(@CurrentUser UserPayload user, @PathVariable Long otherId) {
        Friendship f = service.getFriend(user.getId(), otherId);

        if (f == null) {
            return null;
        }
        return new FriendshipResponse(f, "user1", "user2");
    }
}
