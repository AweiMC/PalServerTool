package com.AweiMC.PalServerTool.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupUtil {

    public static void copyDirectory(Path source, Path target) throws IOException {
        // 如果目标目录存在，就删除它
        if (Files.exists(target)) {
            Files.walkFileTree(target, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        // 复制目录
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(file);
                Path destination = target.resolve(relativePath);
                Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(dir);
                Path destination = target.resolve(relativePath);
                Files.createDirectories(destination);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void zipDirectory(Path source, Path target) throws IOException {
        // 获取备份的根目录名称

        // 确保目标目录存在
        Files.createDirectories(target.getParent());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(target))) {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = source.relativize(file);

                    // 截去根目录名称，只保留目录下的相对路径
                    String entryName = relativePath.toString();

                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zipOutputStream.putNextEntry(zipEntry);
                    Files.copy(file, zipOutputStream);
                    zipOutputStream.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        // 返回生成的压缩文件的文件名
        Paths.get(target.getFileName().toString());
    }


}
