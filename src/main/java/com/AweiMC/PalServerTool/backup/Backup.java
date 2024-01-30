package com.AweiMC.PalServerTool.backup;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Backup {
    public static void checkFile(String args, boolean run) {
        if(!run) return;
        File fileToCheck = new File(args);

        if (!fileToCheck.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        String fileName = fileToCheck.getName();
        File currentDirectory = new File(".");
        File[] filesInCurrentDirectory = currentDirectory.listFiles();

        if (filesInCurrentDirectory != null) {
            for (File file : filesInCurrentDirectory) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    // File with the same name exists in the current directory
                    String backupFileName = generateBackupFileName(fileName);
                    File backupFile = new File(backupFileName);

                    // Rename the existing file
                    if (file.renameTo(backupFile)) {
                        System.out.println("File renamed: " + backupFileName);
                    } else {
                        System.out.println("Failed to rename file.");
                    }

                    break; // No need to check further
                }
            }
        }
    }

    private static String generateBackupFileName(String originalFileName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        int dotIndex = originalFileName.lastIndexOf('.');
        String nameWithoutExtension = dotIndex != -1 ? originalFileName.substring(0, dotIndex) : originalFileName;
        String extension = dotIndex != -1 ? originalFileName.substring(dotIndex) : "";

        return nameWithoutExtension + "_" + timestamp + extension;
    }
}
