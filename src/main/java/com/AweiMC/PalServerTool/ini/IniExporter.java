package com.AweiMC.PalServerTool.ini;

import com.AweiMC.PalServerTool.Main;
import com.AweiMC.PalServerTool.gui.pange.ConfigPage;
import com.AweiMC.PalServerTool.util.I18nManager;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class IniExporter {
    private static String fileTimeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return dateFormat.format(new Date());
    }
    public static String mapToIniString(Map<String, String> configMap) {
        String time = "#" + I18nManager.getMessage("message.ini.gen.time") + fileTimeText();
        String info = "#" + String.format(I18nManager.getMessage("message.ini.gen.info.2"), Main.Name);

        // 添加头部

        return time + System.lineSeparator() + info +  System.lineSeparator() +"[/Script/Pal.PalGameWorldSettings]" + System.lineSeparator() +

                // 添加Key和值
                "OptionSettings=" + mapToIniStringValues(configMap) + System.lineSeparator();
    }

    private static String mapToIniStringValues(Map<String, String> configMap) {
        StringBuilder keyValuePairs = new StringBuilder();

        // 遍历配置项，将Key和值组装成INI格式
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 如果值包含空格或者等号，使用双引号括起来

            if (value.contains(" ") || value.contains("=")) {
                keyValuePairs.append(key).append("=\"").append(value).append("\",");
            } else {
                keyValuePairs.append(key).append("=").append(value).append(",");
            }
        }


        // 删除末尾的逗号
        if (!keyValuePairs.isEmpty()) {
            keyValuePairs.setLength(keyValuePairs.length() - 1);
        }



        return "(" + keyValuePairs +")" ;
    }

    public static void exportToIniFile(Map<String, String> configMap, String filePath) {
        String iniString = mapToIniString(configMap);

        try {
            Path path = Paths.get(filePath); // 将字符串路径转换为 Path 对象
            Files.writeString(path, iniString);
            System.out.println("INI file exported successfully to: " + path.toAbsolutePath());
            JOptionPane.showMessageDialog(ConfigPage.page,
                    I18nManager.getMessage("message.ini.out.ok") + path.toAbsolutePath(),
                    I18nManager.getMessage("message.ini.out.ok.title"),
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            System.err.println("Error exporting INI file: " + e.getMessage());
            JOptionPane.showMessageDialog(ConfigPage.page,
                    I18nManager.getMessage("message.ini.out.error")+e.getMessage(),
                    I18nManager.getMessage("message.ini.out.error.title"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
