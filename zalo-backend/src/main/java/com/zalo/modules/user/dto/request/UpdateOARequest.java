package com.zalo.modules.user.dto.request;

import com.zalo.modules.user.entities.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOARequest {
    AccountType aType;
}
