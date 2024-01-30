package com.AweiMC.PalServerTool.config;

import com.AweiMC.PalServerTool.Main;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PTSConfig {
    public int backupInt=0;
    public String backupDate;
    public String backupFileName="Saved_backups";
    public String backupPath;
    public int backupTime=1800;
    public String backupOutPath= Main.Name;
    public String backupLastFileName;
    public String backupLastPath;
    public String backupLastPathName;
    public String iniFilePath;
    public String steamPath;
    public String ServerPath;
    public int autoRestart;
}
