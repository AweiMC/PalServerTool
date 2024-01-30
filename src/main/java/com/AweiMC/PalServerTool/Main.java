package com.AweiMC.PalServerTool;

import com.AweiMC.PalServerTool.gui.InitGUI;

import javax.swing.*;

public class Main {
    public static String Name = "PalServerTool";
    public static double Ver = 1.00;
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        InitGUI.init();
        autoSysGC();
    }

    private static void autoSysGC() {
        Timer timer = new Timer(300000, e -> System.gc());
        timer.start();
    }
}