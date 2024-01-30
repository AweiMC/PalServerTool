package com.AweiMC.PalServerTool.util.system.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExecuteCMD {
    public static void executeCommand(List<String> command) throws IOException, InterruptedException {
        // 创建 ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        // 启动进程
        Process process = processBuilder.start();

        // 读取命令输出
        StringBuilder outputBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("Command executed with exit code: " + exitCode);

        // 返回输出结果
    }

    public static void runCppCmdAndDelete(String destinationPath) {
        try {
            List<String> command = new ArrayList<>();
            command.add(destinationPath);
            command.add("/install");
            command.add("/quiet");
            command.add("/norestart");
            // 执行静默安装
            executeCommand(command);

            // 删除下载的安装文件
            deleteFile(destinationPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void runDixCmdAndDelete(String destinationPath) {
        try {
            // 执行静默安装
            List<String> command = new ArrayList<>();
            command.add(destinationPath);
            command.add("/Q");
            executeCommand(command);

            // 删除下载的安装文件
            deleteFile(destinationPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            // 删除文件
            Files.delete(path);
            System.out.println("File deleted successfully: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
