package com.anthunt.terraform.generator.aws.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    public static String toPrettyFormat(String jsonString) {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }

}
