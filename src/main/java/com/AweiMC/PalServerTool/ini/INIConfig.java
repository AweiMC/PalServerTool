package com.AweiMC.PalServerTool.ini;

import java.io.File;

import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.config.PTSConfig;
import org.ini4j.Ini;
import org.ini4j.Profile;


import java.io.IOException;

public class INIConfig {
    private static PTSConfig cfg = Config.loadConfig();
    public static void init() {
        try {
            String path;
            path=cfg.iniFilePath;
            if (path == null)  {
                System.err.println("error INI File");
            } else {
                System.out.println("load INI : " + path);
                // 指定要读取的 INI 文件路径

                // 创建 Ini 对象
                String[] keyValuePairs = getStrings(path);

                // 创建 PalServerConfig 实例

                // 遍历键值对
                for (String pair : keyValuePairs) {
                    // 按等号拆分键和值
                    String[] keyValue = pair.trim().split("=");

                    // 获取键和值
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    PalServerConfig.setConfigValue(key, value);
                    //System.out.println("Key: " + key + " Value: " + value);


                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] getStrings(String path) throws IOException {
        Ini ini = new Ini(new File(path));

        // 获取指定 section 的配置
        Profile.Section section = ini.get("/Script/Pal.PalGameWorldSettings");
        //var OPS = section.get("OptionSettings");

        String valueString = section.get("OptionSettings");

        // 移除括号，得到内容
        valueString = valueString.substring(1, valueString.length() - 1);
        // 按逗号拆分键值对
        return valueString.split(",");
    }


}


