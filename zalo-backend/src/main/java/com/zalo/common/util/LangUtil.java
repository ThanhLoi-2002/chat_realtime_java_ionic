package com.zalo.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LangUtil {
    // get lang from header of request
    public static String getLang() {

        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) return "vi";

        HttpServletRequest request = attr.getRequest();

        String lang = request.getHeader("Accept-Language");

        return lang == null ? "vi" : lang;
    }
}
