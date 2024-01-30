package com.AweiMC.PalServerTool.util;

import com.AweiMC.PalServerTool.gui.pange.BackupPage;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class RestoreUtil {

    public static void restoreBackup(String backupLastPathName, String backupLastFileName, String restorePath) {
        try {
            // 构建备份文件的完整路径
            Path backupFilePath = Paths.get(backupLastPathName, backupLastFileName);

            // 构建还原目录的完整路径
            Path restoreDirectoryPath = Paths.get(restorePath);

            // 确保还原目录存在
            Files.createDirectories(restoreDirectoryPath);

            // 删除还原目录下已存在的文件
            cleanDirectory(restoreDirectoryPath);

            // 进行解压操作
            unzipDirectory(backupFilePath, restoreDirectoryPath);

            System.out.println("Restore successful.");
            JOptionPane.showMessageDialog(BackupPage.page, I18nManager.getMessage("message.backup.use.done"), I18nManager.getMessage("message.backup.run.done.title"), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(BackupPage.page, I18nManager.getMessage("message.backup.use.error.2")+e.getMessage(), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.err.println("Restore Error: " + e.getMessage());
        }
    }




    private static void unzipDirectory(Path source, Path target) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(source))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // 构建目标路径，使用 resolve 方法来确保正确的路径拼接
                Path entryPath = target.resolve(entry.getName());

                // 如果是目录，就创建目录
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    // 如果是文件，就写入文件
                    // 先确保目标文件的父目录存在
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private static void cleanDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    // 只删除目录下的文件，而不删除目录本身
                    if (!dir.equals(directory)) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

}
