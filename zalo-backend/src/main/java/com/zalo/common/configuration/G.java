package com.zalo.common.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;

public class G {
    // use for log object
    public static String toJson(Object data) {
        Gson prettyGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Proxy.class, new TypeAdapter<Proxy>() {
                    @Override
                    public void write(JsonWriter out, Proxy value) throws IOException {
                        // Define how you want the Proxy represented in JSON
                        out.value(value.toString());
                    }
                    @Override
                    public Proxy read(JsonReader in) throws IOException {
                        return null; // Proxies are typically not deserialized this way
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        return prettyGson.toJson(data);
    }
}
