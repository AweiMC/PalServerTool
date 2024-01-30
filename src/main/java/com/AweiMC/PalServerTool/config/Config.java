package com.AweiMC.PalServerTool.config;

import com.AweiMC.PalServerTool.Main;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static final String CONFIG_PATH =  Main.Name + File.separator + "Config.toml";
    public static PTSConfig loadConfig() {
        try {
            File configFile = new File(CONFIG_PATH);
            if (!configFile.exists()) {
                return new PTSConfig(); // 返回默认配置，或者抛出异常
            }

            TomlMapper tomlMapper = new TomlMapper();
            return tomlMapper.readValue(configFile, PTSConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new PTSConfig(); // 返回默认配置，或者抛出异常
        }
    }

    public static void saveConfig(PTSConfig config) {
        try {
            TomlMapper tomlMapper = new TomlMapper();

            File configFile = new File(CONFIG_PATH);
            tomlMapper.writeValue(configFile, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}