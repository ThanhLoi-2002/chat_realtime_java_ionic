package com.zalo.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class G {
    // use for log object
    public static String toJson(Object data){
        Gson prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        return prettyGson.toJson(data);
    }
}
