package com.zalo.mapper;

import com.zalo.dto.request.Lang.LangCreationRequest;
import com.zalo.dto.request.Lang.LangUpdateRequest;
import com.zalo.dto.response.Lang.LangResponse;
import com.zalo.model.Lang;

import java.util.Collections;
import java.util.List;

public class LangMapper extends BaseMapper {

    // Map từ entity -> response DTO
    public static LangResponse toResponse(Lang e) {
        if (e == null) return null;
        LangResponse r = new LangResponse();

        // map base fields
        mapBase(e, r);

        r.setId(e.getId());
        r.setCode(e.getCode());
        r.setEn(e.getEn());
        r.setVi(e.getVi());
        r.setTw(e.getTw());
        r.setCn(e.getCn());
        return r;
    }

    // Map từ creation DTO -> entity (tạo mới)
    public static Lang toEntity(LangCreationRequest req) {
        if (req == null) return null;
        Lang e = new Lang();
        // Note: id left null (auto-generated)
        e.setCode(req.getCode());
        // map DTO short names to entity fields
        e.setEn(req.getEn());
        e.setVi(req.getVi());
        e.setTw(req.getTw());
        e.setCn(req.getCn());
        return e;
    }

    // Update existing entity from update DTO (partial update)
    // Only non-null fields in req will overwrite entity fields.
    public static void updateEntityFromDto(LangUpdateRequest req, Lang e) {
        if (req == null || e == null) return;
        if (req.getCode() != null) e.setCode(req.getCode());
        if (req.getEn() != null) e.setEn(req.getEn());
        if (req.getVi() != null) e.setVi(req.getVi());
        if (req.getTw() != null) e.setTw(req.getTw());
        if (req.getCn() != null) e.setCn(req.getCn());
    }

    // Optional: map list of entities to list of responses
    public static List<LangResponse> toResponseList(List<Lang> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(LangMapper::toResponse).toList();
    }
}
