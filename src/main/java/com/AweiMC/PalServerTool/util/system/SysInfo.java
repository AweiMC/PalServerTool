package com.AweiMC.PalServerTool.util.system;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

public class SysInfo {
    private static final SystemInfo systemInfo = new SystemInfo();

    public static int getCPUUasge() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();

        // 获取两个时间点的 CPU 使用情况
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        sleep(); // 等待一段时间

        // 计算 CPU 使用率
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks);

        // 将 CPU 使用率转换为百分比

        return (int) (cpuUsage * 100);
    }
    public static String getCPUUsage(int usg) {
        if (usg < 0 || usg > 100) {
            return "Out of range";
        }
        int numEquals = usg / 10;
        return "=".repeat(numEquals) + " " +
                usg + "%";
    }

    public static String getCPUName() {
        SystemInfo systemInfo = new SystemInfo();
        // 获取 CPU 信息
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        return processor.getProcessorIdentifier().getName();
    }
    public static String getRamUsage() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;

        return formatBytes(usedMemory);
    }
    public static String getRamTotal() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalMemory = memory.getTotal();

        return formatBytes(totalMemory);
    }
    public static String getRamInfo() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        return (usedMemory * 100 / totalMemory) + "%";
    }
    public static int getRamInt() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        return (int) (usedMemory * 100 / totalMemory);
    }

    private static String formatBytes(long bytes) {
        // 格式化字节数为可读字符串
        long kilobytes = bytes / 1024;
        long megabytes = kilobytes / 1024;
        long gigabytes = megabytes / 1024;

        if (gigabytes > 0) {
            return gigabytes + " GB";
        } else if (megabytes > 0) {
            return megabytes + " MB";
        } else {
            return kilobytes + " KB";
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public static int getRemainingSpaceInGB(String directoryPath) {
        File specifiedDirectory = new File(directoryPath);
        long freeSpace = specifiedDirectory.getFreeSpace(); // 返回的是字节

        // 将字节转换为GB
        long gbFactor = 1024 * 1024 * 1024;
        long freeSpaceInGB = freeSpace / gbFactor;

        return (int) freeSpaceInGB;
    }

    public static String getRemainingSpace(String directoryPath) {
        try {
            Path path = Path.of(directoryPath);

            FileStore fileStore = Files.getFileStore(path);
            long totalSpace = fileStore.getTotalSpace();
            long usableSpace = fileStore.getUsableSpace();

            // 计算剩余空间百分比
            double remainingSpacePercentage = (double) usableSpace / totalSpace * 100;

            // 单位换算

            return getString(usableSpace, remainingSpacePercentage);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calculating remaining space"; // 如果出现异常，返回错误信息
        }
    }
    public static int getSpace(String directoryPath) {
        try {
            Path path = Path.of(directoryPath);

            FileStore fileStore = Files.getFileStore(path);
            long totalSpace = fileStore.getTotalSpace();
            long usableSpace = fileStore.getUsableSpace();

            // 计算剩余空间百分比
            double remainingSpacePercentage = (double) usableSpace / totalSpace * 100;
            return (int) (100-remainingSpacePercentage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static String getString(long usableSpace, double remainingSpacePercentage) {
        String unit = "B";
        double remainingSpace = usableSpace;

        if (remainingSpace >= 1024) {
            remainingSpace /= 1024;
            unit = "KB";
        }

        if (remainingSpace >= 1024) {
            remainingSpace /= 1024;
            unit = "MB";
        }

        if (remainingSpace >= 1024) {
            remainingSpace /= 1024;
            unit = "GB";
        }

        // 格式化输出
        return String.format("%.2f%% (%.2f %s)", remainingSpacePercentage, remainingSpace, unit);
    }


}
