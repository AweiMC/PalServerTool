package com.AweiMC.PalServerTool.util.system;

public class OSInfo {
    public static Enum<OSEnum> os;

    public static void OSInit() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            System.out.println("Windows");
            os=OSEnum.WINDOWS;
        } else if (osName.contains("nix") ) {
            System.out.println("Linux");
            os=OSEnum.LINUX;
        } else if (osName.contains("nux")){
            System.out.println("Unix");
            os=OSEnum.UNIX;
        } else if ( osName.contains("mac")) {
            System.out.println("mac");
            os=OSEnum.MAC;
        } else  {
            System.out.println("Other");
            os=OSEnum.OTHER;
        }
    }
   public enum OSEnum {
        WINDOWS,
        LINUX,
        UNIX,
        MAC,
        OTHER
    }
}
