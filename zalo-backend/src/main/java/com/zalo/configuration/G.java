package com.zalo.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class G {
    // use for log object
    public static String toJson(Object data) {
        Gson prettyGson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        return prettyGson.toJson(data);
    }
}
