package com.AweiMC.PalServerTool.util.system.windows;

import java.io.IOException;

public class RuntimeLib {
    public static boolean Cpp=false;
    public static boolean Dx=false;
    public static void isCppRuntimeInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("where msvcp140.dll /Q");
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("msvcp140.dll found. Visual C++ runtime may be installed.");
                Cpp=true;
            } else {
                Cpp=false;
                System.out.println("msvcp140.dll not found. Visual C++ runtime may not be installed.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Cpp=false;
        }
    }

    public static void isDirectXInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("where dxdiag.exe /Q");
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("dxwebsetup.exe found. DirectX may be installed.");
                Dx=true;
            } else {
                System.out.println("dxwebsetup.exe not found. DirectX may not be installed.");
                Dx=false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Dx=false;
        }
    }

}
