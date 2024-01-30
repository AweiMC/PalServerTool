package com.AweiMC.PalServerTool.gui;

import com.AweiMC.PalServerTool.Main;
import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.gui.pange.*;
import com.AweiMC.PalServerTool.ini.INIConfig;
import com.AweiMC.PalServerTool.util.I18nManager;
import com.AweiMC.PalServerTool.util.system.OSInfo;
import com.AweiMC.PalServerTool.util.RunTime;
import com.AweiMC.PalServerTool.util.system.windows.RuntimeLib;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class InitGUI {

    private static void allInit() throws UnsupportedLookAndFeelException {
        OSInfo.OSInit();
        I18nManager.setLanguage("zh_CN");
        UIManager.setLookAndFeel(new FlatLightLaf());
        RunTime.init();
        RuntimeLib.isCppRuntimeInstalled();
        RuntimeLib.isDirectXInstalled();
    }

    public static void init() throws UnsupportedLookAndFeelException {
        allInit();

        // 在主方法中创建 JFrame 实例并使用初始化后的 main 变量
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(Main.Name);
            try {
                initGUI(frame);
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setVisible(true);
        });
    }

    public static void initGUI(JFrame frame) throws UnsupportedLookAndFeelException {
        // 设置FlatLaf的主题为FlatIntelliJLaf（或者FlatDarkLaf、FlatLightLaf）


        // 创建工具栏
        JTabbedPane tabbedPane = new JTabbedPane();

        // 添加 MainInterface 和 ConfigPage 到 tabbedPane
        tabbedPane.addTab(I18nManager.getMessage("message.gui.main"), MainInterface.page);
        tabbedPane.addTab(I18nManager.getMessage("message.gui.local.server"), LocalServerPage.page);
        tabbedPane.addTab(I18nManager.getMessage("message.gui.rcon"), RCONPage.page);
        tabbedPane.addTab(I18nManager.getMessage("message.gui.config"), ConfigPage.page);
        tabbedPane.addTab(I18nManager.getMessage("message.gui.backup"), BackupPage.page);
        tabbedPane.addTab(I18nManager.getMessage("message.gui.install.server"), InstallPage.page);

        // 设置 frame 的 contentPane 为 tabbedPane
        frame.setContentPane(tabbedPane);

        // 设置默认关闭操作
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopTimer();
            }});

        // 添加其他初始化代码
        if(Config.loadConfig().iniFilePath!=null) INIConfig.init();
        LocalServerPage.addPage();
        InstallPage.addPage();
        MainInterface.addPage();
        RCONPage.addPage();
        ConfigPage.addPage();
        BackupPage.addPage();
    }
    private static void stopTimer() {
        // 停止定时器
        if (MainInterface.timer != null && MainInterface.timer.isRunning()) {
            MainInterface.timer.stop();
        }
    }
}
