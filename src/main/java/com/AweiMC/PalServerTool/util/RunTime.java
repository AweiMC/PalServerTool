package com.AweiMC.PalServerTool.util;

import java.util.Timer;
import java.util.TimerTask;

public class RunTime {
    private static final Timer timer = new Timer();
    private static long seconds = 0;
    public static void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seconds++;
            }
        }, 0, 1000);
    }
    public static String getRunTime() {
        return formatElapsedTime(seconds);
    }
    public static String formatElapsedTime(long seconds) {
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = ((seconds % (24 * 3600)) % 3600) / 60;
        long remainingSeconds = ((seconds % (24 * 3600)) % 3600) % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(String.format(I18nManager.getMessage("message.time.day"), days));
        }

        if (hours > 0 || !result.isEmpty()) {
            result.append(String.format(I18nManager.getMessage("message.time.hours"), hours));
        }

        if (minutes > 0 || !result.isEmpty()) {
            result.append(String.format(I18nManager.getMessage("message.time.minutes"), minutes));
        }

        result.append(String.format(I18nManager.getMessage("message.time.seco"), remainingSeconds));

        return result.toString();
    }

}
