package com.AweiMC.PalServerTool.util.system.linux;

import java.util.ArrayList;
import java.util.List;

public class ExecuteBash {

    public static List<String> convertToShellScript(String steamPath) {
        List<String> script = new ArrayList<>();

        String current_user = System.getProperty("user.name");

        script.add("#!/bin/bash");
        script.add("");
        script.add("sudo add-apt-repository multiverse -y");
        script.add("sudo dpkg --add-architecture i386");
        script.add("sudo apt update -y");
        script.add("sudo apt-get remove needrestart -y");
        script.add("");
        script.add("echo steam steam/license note '' | sudo debconf-set-selections");
        script.add("echo steam steam/question select \"I AGREE\" | sudo debconf-set-selections");
        script.add("sudo apt install steamcmd -y");
        script.add("");
        script.add("mkdir -p " + steamPath + "/.steam/sdk64/");
        script.add("steamcmd +login anonymous +app_update 1007 +quit");
        script.add("steamcmd +login anonymous +app_update 2394010 validate +quit");
        script.add("");
        script.add("cp " + steamPath + "/Steam/steamapps/common/Steamworks\\ SDK\\ Redist/linux64/steamclient.so " + steamPath + "/.steam/sdk64/");
        script.add("");
        script.add("cat <<EOF > " + steamPath + "/pal-server-run.sh");
        script.add("#!/bin/bash");
        script.add("");
        script.add("cd " + steamPath + "/Steam/steamapps/common/PalServer");
        script.add("(./PalServer.sh >> /tmp/PalServer.log &)");
        script.add("EOF");
        script.add("");
        script.add("chmod +x " + steamPath + "/pal-server-run.sh");
        script.add("mv " + steamPath + "/pal-server-run.sh " + steamPath + "/Steam/steamapps/common/PalServer");
        script.add("");
        script.add("cat <<EOF > " + steamPath + "/pal-server.service");
        script.add("[Unit]");
        script.add("Description=pal-server.service");
        script.add("");
        script.add("[Service]");
        script.add("Type=forking");
        script.add("User=" + current_user);
        script.add("Restart=on-failure");
        script.add("RestartSec=30s");
        script.add("ExecStart=" + steamPath + "/Steam/steamapps/common/PalServer/pal-server-run.sh -useperfthreads -NoAsyncLoadingThread -UseMultithreadForDS");
        script.add("");
        script.add("[Install]");
        script.add("WantedBy=multi-user.target");
        script.add("EOF");
        script.add("");
        script.add("sudo mv " + steamPath + "/pal-server.service /usr/lib/systemd/system/");
        script.add("sudo systemctl enable pal-server");
        script.add("sudo systemctl restart pal-server");

        return script;
    }


}
