package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.util.*;
import com.AweiMC.PalServerTool.config.Config;
import com.AweiMC.PalServerTool.config.PTSConfig;
import com.AweiMC.PalServerTool.util.system.SysInfo;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.AweiMC.PalServerTool.config.Config.saveConfig;

public class BackupPage {
    public static JPanel page = new JPanel(null); // 使用 null 布局
    private static final PTSConfig cfg = Config.loadConfig();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> scheduledFuture;
    private static boolean autoBackup=false;
    private static JCheckBox onAutoBackup = new JCheckBox(I18nManager.getMessage("message.backup.auto"));

    private static void startBackup() {
        try {
            // 源目录
            if (cfg.backupPath == null) {
                // 处理 cfg.backupPath 为 null 的情况，可以抛出异常或者进行其他处理
                System.out.println("Backup Path is null");
                return;
            }

            Path sourcePath = Paths.get(cfg.backupPath, "Pal", "Saved", "SaveGames");

            // 目标目录
            Path targetPath = Paths.get(cfg.backupOutPath, "temp");
            var sorPath = cfg.backupPath + File.separator + "Pal" + File.separator + "Saved" + File.separator + "SaveGames";
            var sp = new File(sorPath);
            String tsp = sp.getParent()+File.separator+"SaveGames";
            System.out.println(tsp);
            cfg.backupLastPath=tsp;//路径，例如E:\TST\PalOptilGuard\steamcmd\palworldserver\Pal\Saved


            //文件输出名称
            String fileName = "["+ fileTimeText() + "]-"+cfg.backupFileName+".zip";
            var pt = new File(cfg.backupOutPath+ File.separator + "Backup");
            String et = pt.getParent() + "Backup";

            System.out.println(et);
            cfg.backupLastPathName = et;
            cfg.backupLastFileName = fileName;//文件名称，例如[2024-01-28_08-12-54]-Saved_backups.zip
            saveConfig(cfg);

            // 压缩文件路径
            Path zipFilePath = Paths.get(cfg.backupOutPath, "Backup", fileName);


            // 复制目录
            BackupUtil.copyDirectory(sourcePath, targetPath);

            // 压缩目录
            BackupUtil.zipDirectory(targetPath, zipFilePath);
            if (!autoBackup)JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.backup.run.done"), I18nManager.getMessage("message.backup.run.done.title"), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.backup.start.error"), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
            System.out.println("Backup Error: " + e.getMessage());
            shutdownScheduler();
            autoBackup = false;
        }
    }


    public static void addPage() {
        int textX = 10;
        int textY = 10;
        int textH = 20;
        int textW = 150;

        JLabel lastBackup = new JLabel(I18nManager.getMessage("message.backup.last") +cfg.backupDate);
        lastBackup.setBounds(textX,textY,textW + 200,textH);

        JLabel backupInt = new JLabel(I18nManager.getMessage("message.backup.int")+cfg.backupInt);
        backupInt.setBounds(textX,textY+15,textW,textH +10);

        JButton startBackup = new JButton(I18nManager.getMessage("message.backup.start"));
        startBackup.setBounds(textX,textY+250,textW,textH +10);//启动备份

        JTextField backupTime = new JTextField();
        backupTime.setBounds(textX+80,textY+43+160,textW-60,textH);
        backupTime.setToolTipText(I18nManager.getMessage("message.backup.time.tooltip"));
        backupTime.setText(String.valueOf(cfg.backupTime));

        JLabel backupTText = new JLabel(I18nManager.getMessage("message.backup.time"));
        backupTText.setBounds(textX,textY+42+160,textW,textH);

        JTextField backupPath = new JTextField(cfg.backupPath);
        backupPath.setBounds(textX+80,textY+43+120,textW-60,textH);
        backupPath.setToolTipText(I18nManager.getMessage("message.backup.path.tooltip"));

        JLabel backupPText = new JLabel(I18nManager.getMessage("message.backup.path"));
        backupPText.setBounds(textX,textY+42+120,textW,textH);

        JButton chooseButton = new JButton(I18nManager.getMessage("message.backup.path.set"));
        chooseButton.setToolTipText(I18nManager.getMessage("message.backup.path.set.tooltip"));
        chooseButton.setBounds(textX+180,textY+42+120,textW-40,textH);//选择目录

        JProgressBar spaceBar = new JProgressBar();
        spaceBar.setBounds(textX+90,textY+50,textW-60, 10);
        if(cfg.backupPath!=null)spaceBar.setValue(SysInfo.getRemainingSpaceInGB(cfg.backupPath));


        JLabel diskSpace = new JLabel(I18nManager.getMessage("message.backup.disk.space"));
        diskSpace.setBounds(textX,textY+39,textW,textH +10);


        JButton useBackupButton = new JButton(I18nManager.getMessage("message.backup.use"));
        useBackupButton.setToolTipText(I18nManager.getMessage("message.backup.use.tooltip"));
        useBackupButton.setBounds(textX+180,textY+250,textW,textH +10);//还原备份
        useBackupButton.addActionListener(e -> {
            if (cfg.backupLastPath != null && cfg.backupLastFileName != null) {
                try {
                    RestoreUtil.restoreBackup(cfg.backupLastPathName,cfg.backupLastFileName,cfg.backupLastPath);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.backup.use.error.2")+ex.getMessage(), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.backup.use.error"), I18nManager.getMessage("message.ini.in.to.error.title"), JOptionPane.ERROR_MESSAGE);
            }
        });


        JLabel backupTName = new JLabel(I18nManager.getMessage("message.backup.file.name"));
        backupTName.setBounds(textX,textY+70-2,textW-60, 20);

        JTextField backupName = new JTextField(cfg.backupFileName);
        backupName.setBounds(textX+90,textY+70,textW-60, 20);//需要get一次
        backupName.setToolTipText(I18nManager.getMessage("message.backup.file.name.tooltip"));

        if(cfg.backupPath==null) {
            backupTName.setVisible(false);
            backupName.setVisible(false);
            spaceBar.setVisible(false);
            diskSpace.setVisible(false);
        }

        JButton setCfgButton = new JButton(I18nManager.getMessage("message.backup.cfg.set"));
        setCfgButton.setToolTipText(I18nManager.getMessage("message.backup.cfg.set.tooltip"));
        setCfgButton.setBounds(textX+180,textY+82+120,textW-40,textH);//保存设置
        setCfgButton.addActionListener(e -> {
            try {
                cfg.backupTime = Integer.parseInt(backupTime.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,I18nManager.getMessage("message.backup.cfg.error"));
                throw new RuntimeException(ex);
            }
        });

        chooseButton.addActionListener(e -> {
            File path = chooseDirectory(page);
            if (path != null) {
                String pt = String.valueOf(path);
                String pn = FileUtil.getPalName();

                if (FileUtil.checkFile(pn, pt)) {
                    onAutoBackup.setEnabled(true);
                    startBackup.setEnabled(true);
                    useBackupButton.setEnabled(true);
                    backupTName.setVisible(true);
                    backupName.setVisible(true);
                    diskSpace.setVisible(true);
                    spaceBar.setVisible(true);
                    spaceBar.setValue(SysInfo.getRemainingSpaceInGB(cfg.backupPath));
                    spaceBar.setToolTipText(I18nManager.getMessage("message.backup.disk.space.2") + SysInfo.getRemainingSpace(pt));
                    backupPath.setText(pt);
                    diskSpace.setEnabled(true);
                } else {
                    onAutoBackup.setEnabled(true);
                    startBackup.setEnabled(true);
                    useBackupButton.setEnabled(true);
                    diskSpace.setVisible(true);
                    spaceBar.setVisible(true);
                    backupTName.setVisible(true);
                    backupName.setVisible(true);
                    spaceBar.setValue(SysInfo.getRemainingSpaceInGB(cfg.backupPath));
                    spaceBar.setToolTipText(I18nManager.getMessage("message.backup.disk.space.2") + SysInfo.getRemainingSpace(pt));
                    JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.backup.un.get"));
                    backupPath.setText(pt);
                }
                spaceBar.setValue(SysInfo.getSpace(pt));
                cfg.backupPath=pt;
                saveConfig(cfg);
            }
        });
        twoPage(startBackup,setCfgButton);

        if (Objects.equals(cfg.backupOutPath, "") || Objects.equals(cfg.backupPath, "")) {
            onAutoBackup.setEnabled(false);
            startBackup.setEnabled(false);
            useBackupButton.setEnabled(false);
        } else {
            onAutoBackup.setEnabled(true);
            startBackup.setEnabled(true);
            useBackupButton.setEnabled(true);
        }


        page.add(setCfgButton);
        page.add(backupTName);
        page.add(backupName);
        page.add(useBackupButton);
        page.add(diskSpace);
        page.add(spaceBar);
        page.add(chooseButton);
        page.add(backupPath);
        page.add(backupPText);
        page.add(backupTText);
        page.add(backupTime);
        page.add(backupInt);
        page.add(startBackup);
        page.add(lastBackup);
        startBackup.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    // 设置新的文本内容
                    setBackup();
                    startBackup();
                    backupInt.setText(I18nManager.getMessage("message.backup.int") + cfg.backupInt);
                    lastBackup.setText(I18nManager.getMessage("message.backup.last") + cfg.backupDate);
                    return null;
                }
                @Override
                protected void process(List<Void> chunks) {
                    // 更新Swing组件，这里可以更新UI上的文本区域等
                    // 注意：这里的参数类型应该与SwingWorker的泛型参数之一相匹配
                    backupInt.setText(chunks.get(0).toString());
                    lastBackup.setText(chunks.get(1).toString());
                }

                @Override
                protected void done() {
                    super.done();
                }
            };

            worker.execute();
        });


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            if(cfg.backupPath!=null)spaceBar.setValue(SysInfo.getRemainingSpaceInGB(cfg.backupPath));
        }, 0, 3, TimeUnit.MINUTES);

    }
    // TODO This TwoPage
    private static void twoPage(JButton startBackup,JButton setCfgButton) {
        int textX = 500;
        int textY = 10;
        int textH = 20;
        int textW = 150;

        onAutoBackup.setBounds(textX,textY-2,textW,textH);

        JLabel backupPText = new JLabel(I18nManager.getMessage("message.backup.auto.path"));
        backupPText.setBounds(textX,textY+20,textW,textH);

        JButton chooseButton = new JButton(I18nManager.getMessage("message.backup.path.set"));
        chooseButton.setToolTipText(I18nManager.getMessage("message.backup.path.set.tooltip"));
        chooseButton.setBounds(textX+270,textY+20,textW-50,textH);//选择目录

        JTextField backupPath = new JTextField(cfg.backupOutPath);
        backupPath.setBounds(textX+110,textY+20,textW,textH);

        chooseButton.addActionListener(e -> {
            File path = chooseDirectory(page);
            if (path != null) {
                String pt = String.valueOf(path);
                backupPath.setText(pt);
                cfg.backupOutPath=pt;
                saveConfig(cfg);

            }
        });



        onAutoBackup.addActionListener(e -> {
            if(onAutoBackup.isSelected()) {
                autoBackup=true;
                autoBackup(onAutoBackup.isSelected());
            } else {
                autoBackup=false;
                shutdownScheduler();
                JOptionPane.showMessageDialog(page, I18nManager.getMessage("message.backup.auto.false"), I18nManager.getMessage("message.backup.run.done.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setCfgButton.addActionListener(e -> {
           cfg.backupOutPath=backupPath.getText();
           System.out.println(cfg.backupOutPath);

           saveConfig(cfg);
        });

        startBackup.addActionListener(e -> {
        });



        page.add(chooseButton);
        page.add(backupPText);
        page.add(backupPath);
        page.add(onAutoBackup);


    }


    private static void autoBackup(boolean run) {
        if (scheduler.isTerminated()) {
            // 创建新的 scheduler
            scheduler = Executors.newScheduledThreadPool(1);
        }

        if (run && (scheduledFuture == null || scheduledFuture.isDone()) || cfg.backupTime > 100) {
            Runnable task = () -> {
                startBackup();
                setBackup();
            };

            scheduledFuture = scheduler.scheduleAtFixedRate(task, 0, cfg.backupTime, TimeUnit.SECONDS);
        } else if (!run && scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }
    }

    // 在应用程序关闭时调用，确保关闭定时任务
    private static void shutdownScheduler() {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;  // 任务被取消，将 scheduledFuture 置为 null
        }
        onAutoBackup.setSelected(false);
        scheduler.shutdown();  // 禁止提交新任务，等待已提交的任务执行完毕
    }

    private static File chooseDirectory(JPanel parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(I18nManager.getMessage("message.backup.choose.directory"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showDialog(parentFrame, I18nManager.getMessage("message.backup.select"));

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected Directory: " + fileChooser.getSelectedFile());
            return fileChooser.getSelectedFile();
        } else {
            System.out.println("User canceled the operation");
        }
        return null;
    }
    private static void setBackup() {
        cfg.backupDate=timeText();
        cfg.backupInt+=1;
        saveConfig(cfg);
    }
    private static String timeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
    private static String fileTimeText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return dateFormat.format(new Date());
    }
}
