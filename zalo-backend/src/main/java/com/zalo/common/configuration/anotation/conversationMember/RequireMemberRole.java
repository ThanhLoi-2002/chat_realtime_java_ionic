package com.zalo.common.configuration.anotation.conversationMember;

import com.zalo.modules.conversation.entities.MemberRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireMemberRole {
    MemberRole[] memberRoles() default {};
}
