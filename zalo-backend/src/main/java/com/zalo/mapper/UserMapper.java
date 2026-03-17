package com.zalo.mapper;

import com.zalo.dto.response.User.UserResponse;
import com.zalo.model.User;

import java.util.Collections;
import java.util.List;

public class UserMapper extends BaseMapper{
    // Map từ entity -> response DTO
    public static UserResponse toResponse(User e) {
        if (e == null) return null;
        UserResponse r = new UserResponse();

        // map base fields
        mapBase(e, r);

        r.setId(e.getId());
        r.setUsername(e.getUsername());
        r.setPhone(e.getPhone());
        r.setAvatar(e.getAvatar());
        r.setCover(e.getCover());
        return r;
    }

    // Map từ creation DTO -> entity (tạo mới)
//    public static User toEntity(LangCreationRequest req) {
//        if (req == null) return null;
//        Lang e = new Lang();
//        // Note: id left null (auto-generated)
//        e.setCode(req.getCode());
//        // map DTO short names to entity fields
//        e.setEn(req.getEn());
//        e.setVi(req.getVi());
//        e.setTw(req.getTw());
//        e.setCn(req.getCn());
//        return e;
//    }

    // Update existing entity from update DTO (partial update)
    // Only non-null fields in req will overwrite entity fields.
//    public static void updateEntityFromDto(LangUpdateRequest req, Lang e) {
//        if (req == null || e == null) return;
//        if (req.getCode() != null) e.setCode(req.getCode());
//        if (req.getEn() != null) e.setEn(req.getEn());
//        if (req.getVi() != null) e.setVi(req.getVi());
//        if (req.getTw() != null) e.setTw(req.getTw());
//        if (req.getCn() != null) e.setCn(req.getCn());
//    }

    // Optional: map list of entities to list of responses
    public static List<UserResponse> toResponseList(List<User> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(UserMapper::toResponse).toList();
    }
}
