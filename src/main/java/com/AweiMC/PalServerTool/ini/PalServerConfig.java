package com.AweiMC.PalServerTool.ini;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PalServerConfig {
    public static final Map<String, String> CONFIG_MAP = new HashMap<>();

    public static String getConfigValue(String key) {
        String value = CONFIG_MAP.get(key);

        if (value != null) {
            // 去除字符串左右的空白字符和双引号
            value = value.trim().replaceAll("^\"|\"$", "");

            if (value.isEmpty()) {
                // 如果去除双引号后字符串为空，返回 null
                return null;
            }
        }

        return value;
    }


    public static void setConfigValue(String key, String value) {
        CONFIG_MAP.put(key, value);
    }
}