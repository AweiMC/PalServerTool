package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.config.PTSConfig;
import com.AweiMC.PalServerTool.gui.Exec.ExecuteCommandAsync;
import com.AweiMC.PalServerTool.util.I18nManager;
import com.AweiMC.PalServerTool.util.RunTime;
import com.AweiMC.PalServerTool.util.system.SysInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LocalServerPage {
    private static final PTSConfig cfg = Config.loadConfig();
    public static JPanel page = new JPanel(null);
    private static boolean serverStart =false;
    public static boolean isAutoStart = false;
    public static void addPage() {
        int X = 10;
        int Y = 10;
        int W = 300;
        int H = 30;
        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.append(I18nManager.getMessage("message.local.init"));
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setBounds(X+450,Y+30,W+200,H+300);

        JTextField startPath = new JTextField();
        startPath.setBounds(X + 570, Y + 380, W + 80, H);
        JButton chooseButton = getButton(startPath);
        chooseButton.setBounds(X + 450, Y + 380, W-200+10, H);


        JTextField cmd = new JTextField();
        cmd.setBounds(X + 570, Y + 430, W + 80, H);
        JButton startArgButton = getSrtArgButton(cmd);
        startArgButton.setBounds(X + 450, Y + 430, W-200+10, H);

        JButton startLServer = new JButton(I18nManager.getMessage("message.local.start"));//启动服务器
        startLServer.setBounds(X, Y + 290, W-200+10, H);
        JButton sendServer = new JButton(I18nManager.getMessage("message.local.send"));//发送指令
        sendServer.setBounds(X, Y + 330, W-200+10, H);
        JTextField LServerCmd = new JTextField();//条
        LServerCmd.setBounds(X + 120, Y + 330, W + 20, H);


        JTextField autoRSTime = new JTextField();//条
        autoRSTime.setBounds(X+80, Y + 430, W-200, H-10);
        JLabel autoRSTimeText = new JLabel(I18nManager.getMessage("message.local.auto.restart.time"));
        autoRSTimeText.setBounds(X, Y + 430, W-200, H-10);

        autoRSTimeText.setToolTipText(I18nManager.getMessage("message.local.auto.restart.time.title"));
        JCheckBox autorestart = new JCheckBox(I18nManager.getMessage("message.local.auto.restart"));
        autorestart.setBounds(X, Y + 400, W-200, H);

        JLabel intro = new JLabel(getServerStartInfo());//信息
        intro.setBounds(X+130, Y + 290, W-200+10, H);
        startLServer.addActionListener(e -> {//启动服务器逻辑部分
            try {
                String str = startPath.getText();
                if (str != null) {
                    List<String> strCmd = new ArrayList<>();
                    strCmd.add(str);
                    strCmd.add(cmd.getText());
                    extracted(startLServer, intro);
                    ExecuteCommandAsync.executeCommandAsync(strCmd,outputTextArea);
                }
            } catch (Exception ex) {
                extracted(startLServer, intro);
                throw new RuntimeException(ex);
            }
        });

        autorestart.addActionListener(e -> {//自动重启
            isAutoStart = autorestart.isSelected();
            JOptionPane.showMessageDialog(null, getBoolInfo(autorestart.isSelected()));
        });

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        AtomicReference<String> previousValue = new AtomicReference<>(autoRSTime.getText());

        scheduler.scheduleAtFixedRate(() -> {
            // 获取当前值
            String currentValue = autoRSTime.getText();

            // 检查当前值和上一次保存的值是否不同
            if (!currentValue.equals(previousValue.get())) {
                // 如果不同，进行保存操作
                int time = Integer.parseInt(currentValue);
                if (time != 0) {
                    cfg.autoRestart = time;
                    Config.saveConfig(cfg);
                    // 更新上一次保存的值为当前值
                    previousValue.set(currentValue);
                }
            }
        }, 0, 3, TimeUnit.SECONDS);
        JLabel info = new JLabel(I18nManager.getMessage("message.local.info.warn"));
        info.setBounds(X,Y+10,W,H);
        JLabel info2 = new JLabel(I18nManager.getMessage("message.local.info.warn.2"));
        info2.setBounds(X,Y+30,W,H);


        page.add(info);
        page.add(info2);
        page.add(intro);
        //page.add(sendServer); 发送指令
        //page.add(LServerCmd); 发送指令

        page.add(autoRSTimeText);
        page.add(autoRSTime);
        page.add(autorestart);
        page.add(startLServer);
        page.add(startArgButton);
        page.add(cmd);
        page.add(chooseButton);

        page.add(startPath);
        page.add(scrollPane);
    }




    private static void extracted(JButton startLServer, JLabel intro) {
        startLServer.setEnabled(true);
        serverStart =false;
        SwingUtilities.invokeLater(() -> intro.setText(getServerStartInfo()));
    }


    private static JButton getButton(JTextField textField) {

        JButton chooseButton = new JButton(I18nManager.getMessage("message.local.file"));

        chooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(page);

            if (result == JFileChooser.APPROVE_OPTION) {
                // 用户选择了文件
                String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                SwingUtilities.invokeLater(() -> textField.setText(selectedFilePath));
                cfg.ServerPath=selectedFilePath;
                Config.saveConfig(cfg);
            }
        });
        return chooseButton;
    }
    private static JButton getSrtArgButton(JTextField textField) {

        JButton chooseButton = new JButton(I18nManager.getMessage("message.local.gen"));

        chooseButton.addActionListener(e -> {
            String arg = "-useperfthreads -NoAsyncLoadingThread -UseMultithreadForDS";

            SwingUtilities.invokeLater(() -> textField.setText(arg));
        });
        return chooseButton;
    }

    private static String getServerStartInfo() {
        return (serverStart ? I18nManager.getMessage("message.local.auto.start.yes") : I18nManager.getMessage("message.local.auto.start.no"));
    }
    private static String getBoolInfo(boolean bool) {
        return (bool ? I18nManager.getMessage("message.local.auto.true") : I18nManager.getMessage("message.local.auto.false" ));
    }
}
