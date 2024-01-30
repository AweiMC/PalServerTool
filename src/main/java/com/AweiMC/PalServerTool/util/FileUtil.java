package com.AweiMC.PalServerTool.util;

import com.AweiMC.PalServerTool.util.system.OSInfo;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    /*
      用于检查当前目录被是否有对应文件
      name就是需要检查的文件,path是目录
      返回Boolean值
     */
    public static boolean checkFile(String name,String path) {
        Path palServerDirectory = FileSystems.getDefault().getPath(path);
        Path filePath = palServerDirectory.resolve(name);
        return Files.exists(filePath);
    }
    //最简单的查询，就查询当前目录的
    public static boolean isFileExists(String filePath) {
        Path path = FileSystems.getDefault().getPath(filePath);
        return Files.exists(path);
    }
    public static String getPalName() {
        if (OSInfo.os == OSInfo.OSEnum.WINDOWS) {
            return "PalServer.exe";
        } else if (OSInfo.os == OSInfo.OSEnum.LINUX) {
            return "PalServer.sh";
        } else if (OSInfo.os == OSInfo.OSEnum.UNIX) {
            return "PalServer.sh";
        } else {
            JOptionPane.showMessageDialog(null,I18nManager.getMessage("message.sys.un"));
            return null;
        }
    }

    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipIn.closeEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

}
