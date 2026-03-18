package com.zalo.mapper;

import com.zalo.dto.response.BaseResponse;
import com.zalo.model.BaseEntity;

public abstract class BaseMapper {

    protected static <E extends BaseEntity, R extends BaseResponse>
    void mapBase(E e, R r) {
        r.setId(e.getId());
        r.setStt(e.getStt());
        r.setCt(e.getCt());
        r.setEt(e.getEt());
        r.setCu(e.getCu());
        r.setEu(e.getEu());
    }
}