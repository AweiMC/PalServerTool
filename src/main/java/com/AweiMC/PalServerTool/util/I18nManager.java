package com.AweiMC.PalServerTool.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class I18nManager {
    private static final Map<String, JSONObject> languageBundles = new HashMap<>();
    private static String currentLanguage = "en_US"; // 默认语言为英语（美国）

    public static void setLanguage(String language) {
        currentLanguage = language;
    }


    public static String getMessage(String key) {
        JSONObject bundle = languageBundles.computeIfAbsent(currentLanguage, lang -> {
            // 根据语言加载资源包
            return loadBundle("assets/lang/" + lang + ".json");
        });

        return bundle.getString(key);
    }

    private static JSONObject loadBundle(String filePath) {
        try (InputStream inputStream = I18nManager.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                return JSON.parseObject(content);
            } else {
                // 如果资源文件不存在，返回空的 JSONObject
                return new JSONObject();
            }
        } catch (IOException e) {
            // 如果读取文件出错，返回空的 JSONObject
            return new JSONObject();
        }
    }


}
