package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.config.PTSConfig;
import com.AweiMC.PalServerTool.gui.Exec.ExecuteCommandAsync;
import com.AweiMC.PalServerTool.util.FileUtil;
import com.AweiMC.PalServerTool.util.I18nManager;
import com.AweiMC.PalServerTool.util.downer.SingleThreadDownloader;
import com.AweiMC.PalServerTool.util.system.OSInfo;
import com.AweiMC.PalServerTool.util.system.linux.ExecuteBash;
import com.AweiMC.PalServerTool.util.system.windows.ExecuteCMD;
import com.AweiMC.PalServerTool.util.system.windows.RuntimeLib;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class InstallPage {
    public static JPanel page = new JPanel(null);
    private static JTextField pathTF;
    private static JButton downStmCmd;
    private static String steamPath;
    private static final JLabel downInfoT = new JLabel();
    private static final PTSConfig cfg = Config.loadConfig();
    private static final boolean Sys = OSInfo.os==OSInfo.OSEnum.WINDOWS;
    public static void addPage() {
        int X = 10;
        int Y = 0;
        int W = 300;
        int H = 30;
        String OSI = String.format(I18nManager.getMessage("message.install.sys.info"),OSInfo.os.name());
        JLabel OS =new JLabel(OSI);
        if(Sys) {
            addDirectXPage();
            addCppPage();
            addWinPage();
        } else {
            addLinuxPage();
        }
        OS.setBounds(X,Y,W,H);
        addEPage();
        page.add(OS);
    }
    private static void addEPage() {
        int X = 10;
        int Y = 30;
        int W = 300;
        int H = 30;
        JButton chooseButton = new JButton(I18nManager.getMessage("message.backup.path.set"));
        chooseButton.setBounds(X,Y+170,W-200,H-10);//选择目录
        pathTF = new JTextField();
        if(cfg.steamPath!=null) pathTF.setText(cfg.steamPath);
        pathTF.setBounds(X+120,Y+170,W-100,H-10);
        chooseButton.addActionListener(e-> {
            File path = chooseDirectory(page);
            if (path != null) {//选择目录
                pathTF.setText(String.valueOf(path));
                if(Sys)downStmCmd.setEnabled(true);
                if(Sys)downStmCmd.setText(getSteamLabel(true));
            }
        });
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> steamPath=pathTF.getText(), 0, 3, TimeUnit.SECONDS);
        page.add(pathTF);
        page.add(chooseButton);

    }
    private static void addLinuxPage() {
        int X = 10;
        int Y = 30;
        int W = 300;
        int H = 30;

        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVisible(false);

        JButton chooseButton = new JButton(I18nManager.getMessage("message.backup.path.set"));
        chooseButton.setBounds(X, Y + 170, W - 200, H - 10);// 选择目录

        pathTF = new JTextField();
        pathTF.setBounds(X + 120, Y + 170, W - 100, H - 10);

        scrollPane.setBounds(X + 520, Y, W + 100, H + 200);

        chooseButton.addActionListener(e -> {
            File path = chooseDirectory(page);
            if (path != null) {
                scrollPane.setVisible(true);
                pathTF.setText(String.valueOf(path));
                ExecuteCommandAsync.executeCommandAsyncWindows(ExecuteBash.convertToShellScript(String.valueOf(path)),
                        outputTextArea, downInfoT);
            }
        });

        page.add(scrollPane); // 将 scrollPane 添加到页面
        page.add(downInfoT);
        page.add(chooseButton);
        page.add(pathTF);
    }

    private static void addWinPage() {
        int X = 10;
        int Y = 40;
        int W = 300;
        int H = 30;
        boolean steam = steamPath!=null;

        downStmCmd = new JButton(getSteamLabel(steam));
        downStmCmd.setEnabled(steam);

        downStmCmd.setBounds(X , Y+128, W-200, H-10);

        downInfoT.setBounds(X,Y+200,W,H-10);
        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVisible(false);

        outputTextArea.append(I18nManager.getMessage("message.install.download.cmd.info") + "\n");
        outputTextArea.append(I18nManager.getMessage("message.install.download.cmd.info.2") + "\n");
        outputTextArea.append(I18nManager.getMessage("message.install.download.cmd.info.3") + "\n");
        outputTextArea.append(I18nManager.getMessage("message.install.download.cmd.info.4") + "\n");
        JButton executeButton = getWinButton(outputTextArea);

        executeButton.setVisible(false);
        scrollPane.setBounds(X + 520, Y ,W+100, H+200);
        executeButton.setBounds(X +120, Y+128, W-200, H-10);
        downInfoT.setEnabled(false);
        downStmCmd.addActionListener(e-> {
            executeButton.setVisible(true);
            scrollPane.setVisible(true);
            String destinationPath = steamPath+File.separator+"steamcmd.zip";
            String fileUrl = "https://steamcdn-a.akamaihd.net/client/installer/steamcmd.zip";
            Thread commandThread = new Thread(() -> {
                try {
                    pathTF.setEnabled(false);
                    SingleThreadDownloader.downloadFile(fileUrl, destinationPath, (bytesRead, fileSize, speed) -> {
                        String progressMessage = String.format(I18nManager.getMessage("message.install.download.info"), SingleThreadDownloader.format(bytesRead), SingleThreadDownloader.format(fileSize), speed);
                        SwingUtilities.invokeLater(() -> downInfoT.setText(progressMessage));
                    });

                    // 下载完成后执行以下代码
                    System.out.println("Download completed!");
                    SwingUtilities.invokeLater(() ->downInfoT.setText(I18nManager.getMessage("message.install.download.done")));

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            });

            commandThread.start();
        });
        page.add(executeButton);
        page.add(scrollPane);
        page.add(downInfoT);
        page.add(downStmCmd);
    }

    private static JButton getWinButton(JTextArea outputTextArea) {
        JButton executeButton = new JButton(I18nManager.getMessage("message.install.exec"));
        executeButton.addActionListener(e -> {
            String destinationPath = steamPath + File.separator + "steamcmd.zip";
            String steamcmd = steamPath + File.separator + "SteamCmd" + File.separator + "steamcmd.exe";
            try {
                cfg.steamPath = steamPath;
                Config.saveConfig(cfg);
                FileUtil.unzip(destinationPath, steamPath+File.separator+"SteamCmd");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            SwingUtilities.invokeLater(() -> InstallPage.downInfoT.setText(I18nManager.getMessage("message.install.download.cmd")));
            List<String> command = new ArrayList<>();
            command.add("powershell.exe");
            command.add("Start-Process");
            command.add("\"" + steamcmd + "\""); // 使用双引号确保路径中包含空格时正确处理
            command.add("-ArgumentList");
            command.add("\"+login\", \"anonymous\", \"+app_update\", \"2394010\", \"validate\", \"+quit\"");
            command.add("-Wait");

            System.out.println(command);


            // 创建异步任务
            ExecuteCommandAsync.executeCommandAsyncWindows(command, outputTextArea,downInfoT);


        });
        return executeButton;
    }


    private static void addDirectXPage() {
        int X = 10;
        int Y = 40;
        int W = 300;
        int H = 30;

        JLabel direT = new JLabel(I18nManager.getMessage("message.install.directx"));
        direT.setBounds(X, Y+22, W, H);
        JLabel dire = new JLabel(getCppStatusLabel());
        dire.setBounds(X + 90, Y+22, W, H);
        JLabel direDT = new JLabel();
        direDT.setBounds(X + 270, Y+8+20, W+100, H-10);
        JButton downDire = new JButton(I18nManager.getMessage("message.install.download")+" DirectX");
        downDire.setBounds(X + 150, Y+28, W-200, H-10);
        downDire.addActionListener(e-> {
            downDire.setEnabled(false);// 下载时候禁止点击
            new Thread(() -> {
                downDireLib(direDT);
                SwingUtilities.invokeLater(() -> {
                    downDire.setEnabled(true);// 下载完成可以点击
                    if(RuntimeLib.Dx) {
                        downDire.setText(I18nManager.getMessage("message.install.download.have"));
                        downDire.setEnabled(false);
                    }
                });
                if(FileUtil.isFileExists("dxwebsetup.exe")) {
                    ExecuteCMD.runDixCmdAndDelete("dxwebsetup.exe");
                }
            }).start();
        });

        downDire.setVisible(!RuntimeLib.Dx);

        page.add(direDT);
        page.add(downDire);
        page.add(dire);
        page.add(direT);


    }
    private static void addCppPage() {
        int X = 10;
        int Y = 30;
        int W = 300;
        int H = 30;

        JLabel cppT = new JLabel(I18nManager.getMessage("message.install.cpp"));
        JButton downCPP = new JButton(I18nManager.getMessage("message.install.download")+" C++");
        JLabel cpp = new JLabel(getCppStatusLabel());
        JLabel cppDT = new JLabel();

        cppDT.setBounds(X + 270, Y+4, W+100, H-10);
        downCPP.addActionListener(e-> {
            downCPP.setEnabled(false);// 下载时候禁止点击
            new Thread(() -> {
                downCppLib(cppDT);
                SwingUtilities.invokeLater(() -> {
                    downCPP.setEnabled(true);// 下载完成可以点击
                    if(RuntimeLib.Cpp) {
                        downCPP.setText(I18nManager.getMessage("message.install.download.have"));
                        downCPP.setEnabled(false);
                    }
                    if(FileUtil.isFileExists("vc_redist.x64.exe")) {
                        ExecuteCMD.runCppCmdAndDelete("vc_redist.x64.exe");
                    }
                });
            }).start();
        });
        downCPP.setVisible(!RuntimeLib.Cpp);


        // 设置字体颜色
        //cpp.setForeground(getDirectXStatusColor());
        cpp.setBounds(X + 90, Y, W, H);
        cppT.setBounds(X, Y, W, H);
        downCPP.setBounds(X + 150, Y+6, W-200, H-10);

        page.add(cppDT);
        page.add(cppT);
        page.add(cpp);
        page.add(downCPP);
    }
    public static void downCppLib(JLabel jl) {
        String fileUrl = "https://aka.ms/vs/17/release/vc_redist.x64.exe";
        String destinationPath = "vc_redist.x64.exe";

        try {
            jl.setEnabled(true);
            jl.setText(I18nManager.getMessage("message.install.download.ing"));
            SingleThreadDownloader.downloadFile(fileUrl, destinationPath, (bytesRead, fileSize, speed) -> {
                String progressMessage = String.format(I18nManager.getMessage("message.install.download.info"), SingleThreadDownloader.format(bytesRead), SingleThreadDownloader.format(fileSize), speed);
                // 更新 Swing 组件
                SwingUtilities.invokeLater(() -> jl.setText(progressMessage));
            });
            System.out.println("Download completed!");
            SwingUtilities.invokeLater(() ->jl.setText(I18nManager.getMessage("message.install.download.done")));
            jl.setEnabled(false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void downDireLib(JLabel jl) {
        String fileUrl = "https://download.microsoft.com/download/1/7/1/1718CCC4-6315-4D8E-9543-8E28A4E18C4C/dxwebsetup.exe";
        String destinationPath = "dxwebsetup.exe";

        try {
            jl.setEnabled(true);
            jl.setText(I18nManager.getMessage("message.install.download.ing"));
            SingleThreadDownloader.downloadFile(fileUrl, destinationPath, (bytesRead, fileSize, speed) -> {
                String progressMessage = String.format(I18nManager.getMessage("message.install.download.info"), SingleThreadDownloader.format(bytesRead), SingleThreadDownloader.format(fileSize), speed);
                // 更新 Swing 组件
                SwingUtilities.invokeLater(() -> jl.setText(progressMessage));
            });
            System.out.println("Download completed!");
            SwingUtilities.invokeLater(() ->jl.setText(I18nManager.getMessage("message.install.download.done")));
            jl.setEnabled(false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static String getCppStatusLabel() {
        boolean isInstalled = RuntimeLib.Cpp;
        return (isInstalled ? I18nManager.getMessage("message.install.yes") : I18nManager.getMessage("message.install.no"));
    }
    private static String getSteamLabel(boolean bool) {
        return (bool ? I18nManager.getMessage("message.install.download.server") : I18nManager.getMessage("message.install.path.no"));
    }

    private static File chooseDirectory(JPanel parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(I18nManager.getMessage("message.backup.choose.directory"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showDialog(parentFrame, I18nManager.getMessage("message.backup.select"));

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected Directory: " + fileChooser.getSelectedFile());
            steamPath= String.valueOf(fileChooser.getSelectedFile());
            return fileChooser.getSelectedFile();
        } else {
            System.out.println("User canceled the operation");
        }
        return null;
    }



}
