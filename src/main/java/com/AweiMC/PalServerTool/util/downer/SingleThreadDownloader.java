package com.AweiMC.PalServerTool.util.downer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SingleThreadDownloader {
    private static final int BUFFER_SIZE = 4096;

    public static void downloadFile(String fileUrl, String destinationPath, DownloadProgressListener listener) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();

        // 获取文件大小
        int fileSize = connection.getContentLength();

        try (InputStream in = connection.getInputStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            int totalBytesRead = 0;

            long startTime = System.currentTimeMillis();
            long currentTime;

            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // 计算下载速度
                currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                double speed = (double) totalBytesRead / (double) elapsedTime * 1000.0 / (1024.0 * 1024.0);

                // 通过接口回调通知下载进度
                if (listener != null) {
                    listener.onProgress(totalBytesRead, fileSize, speed);
                }
            }
        }
    }
    public static String format(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public interface DownloadProgressListener {
        void onProgress(int bytesRead, int fileSize, double speed);
    }
}
