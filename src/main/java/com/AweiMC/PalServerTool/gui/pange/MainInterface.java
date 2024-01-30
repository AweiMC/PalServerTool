package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.gui.news.getJson;
import com.AweiMC.PalServerTool.util.I18nManager;
import com.AweiMC.PalServerTool.util.RunTime;
import com.AweiMC.PalServerTool.util.system.SysInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainInterface {
    public static Timer timer;
    public static JPanel page = new JPanel(null); // 使用 null 布局
    private static JComboBox<String> interfaceComboBox;
    private static JLabel resultLabel;
    private static JLabel uploadLabel;
    private static JLabel downloadLabel;

    private static NetworkIF selectedNetworkInterface;
    public static void addPage() {
        int TextX = 10;
        int Th = 50;
        int Tw = 500;
        addNetWorkPage();
        //addTimePage();
        
        var AL = new JLabel(I18nManager.getMessage("message.gui.1"));
        AL.setBounds(TextX, 10, Tw, Th);
        var BL = new JLabel(I18nManager.getMessage("message.gui.2"));
        BL.setBounds(TextX, 30, Tw, Th);
        var CL = new JLabel(I18nManager.getMessage("message.gui.3"));
        CL.setBounds(TextX, 50, Tw, Th);
        var DL = new JLabel(I18nManager.getMessage("message.gui.4"));
        DL.setBounds(TextX, 70, Tw, Th);
        var EL = new JLabel(I18nManager.getMessage("message.gui.5"));
        EL.setBounds(TextX, 90, Tw, Th);
        var FL = new JLabel(I18nManager.getMessage("message.gui.6"));
        FL.setBounds(TextX, 110, Tw, Th);
        var TL = new JLabel(I18nManager.getMessage("message.gui.7"));
        TL.setBounds(TextX, 130, Tw, Th);
        var GL = new JLabel(I18nManager.getMessage("message.gui.8"));
        GL.setBounds(TextX, 150, Tw, Th);
        var HL = new JLabel(I18nManager.getMessage("message.gui.9"));
        HL.setBounds(TextX, 170, Tw, Th);

        var CPUN = new JLabel(I18nManager.getMessage("message.cpu.name"));
        CPUN.setBounds(TextX,200,Tw,Th);
        var CPUNT = new JLabel(SysInfo.getCPUName());
        CPUNT.setBounds(TextX + 140,200,Tw,Th);

        var CPUU = new JLabel(I18nManager.getMessage("message.cpu.usage") );
        CPUU.setBounds(TextX,220,Tw,Th);

        var MT = new JLabel(I18nManager.getMessage("message.ram.usage"));
        MT.setBounds(TextX,240,Tw,Th);
        var RT = new JLabel(I18nManager.getMessage("message.ram.total") + SysInfo.getRamTotal());
        RT.setBounds(TextX,260,Tw,Th);

        var RUNTIME = new JLabel(I18nManager.getMessage("message.run.time") + RunTime.getRunTime());
        RUNTIME.setBounds(TextX,280,Tw,Th);

        JProgressBar cpuBar = new JProgressBar();
        cpuBar.setMaximum(100);
        cpuBar.setBounds(TextX+140,240,Tw-360, 10);
        JProgressBar ramBar = new JProgressBar();
        ramBar.setMaximum(100);
        ramBar.setBounds(TextX+140,260,Tw-360, 10);

        var CPUUT = new JLabel(cpuBar.getValue()+"%");
        CPUUT.setBounds(TextX+285,220,Tw,Th);

        var RTT = new JLabel(ramBar.getValue()+"%");
        RTT.setBounds(TextX+285,240,Tw,Th);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            RUNTIME.setText(I18nManager.getMessage("message.run.time") + RunTime.getRunTime());
            ramBar.setValue(SysInfo.getRamInt());
            CPUUT.setText(cpuBar.getValue()+"%");
            RTT.setText(ramBar.getValue()+"%");
            cpuBar.setValue(SysInfo.getCPUUasge());
        }, 0, 1, TimeUnit.SECONDS);
        page.add(RTT);
        page.add(CPUNT);

        page.add(ramBar);
        page.add(cpuBar);
        page.add(CPUUT);
        page.add(AL);
        page.add(BL);
        page.add(Box.createVerticalStrut(10));
        page.add(CL);
        page.add(DL);
        page.add(EL);
        page.add(FL);
        page.add(TL);
        page.add(GL);
        page.add(HL);
        page.add(CPUU);
        page.add(CPUN);
        page.add(MT);
        page.add(RT);
        page.add(RUNTIME);

    }
    private static void addNetWorkPage() {
        int TextX = 200;
        int Th = 50;
        int Tw = 100;
        int Y = 10;
        interfaceComboBox = new JComboBox<>();
        JButton selectButton = new JButton(I18nManager.getMessage("message.backup.select"));
        JLabel resultLabel = new JLabel();
        uploadLabel = new JLabel();
        downloadLabel = new JLabel();

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        List<NetworkIF> networkIFs = hal.getNetworkIFs();

        for (NetworkIF networkIF : networkIFs) {
            interfaceComboBox.addItem(networkIF.getDisplayName());
        }

        selectButton.addActionListener(e -> {
            String selectedInterface = (String) interfaceComboBox.getSelectedItem();
            selectedNetworkInterface = findNetworkInterfaceByName(networkIFs, selectedInterface);
            resultLabel.setText(I18nManager.getMessage("message.gui.info") + selectedInterface);
        });

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            if (selectedNetworkInterface != null) {
                selectedNetworkInterface.updateAttributes();
                long bytesSent = selectedNetworkInterface.getBytesSent();
                long bytesRecv = selectedNetworkInterface.getBytesRecv();

                try {
                    Thread.sleep(1000); // 等待1秒
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                selectedNetworkInterface.updateAttributes();
                long bytesSent1 = selectedNetworkInterface.getBytesSent();
                long bytesRecv1 = selectedNetworkInterface.getBytesRecv();

                long uploadSpeed = (bytesSent1 - bytesSent) * 8 / 1024; // 计算上传速度（kbps）
                long downloadSpeed = (bytesRecv1 - bytesRecv) * 8 / 1024; // 计算下载速度（kbps）

                SwingUtilities.invokeLater(() -> {
                    uploadLabel.setText(I18nManager.getMessage("message.gui.upload") + formatSpeed(uploadSpeed));
                    downloadLabel.setText(I18nManager.getMessage("message.gui.download") + formatSpeed(downloadSpeed));
                });
            }
        }, 0, 1, TimeUnit.SECONDS);

        interfaceComboBox.setBounds(TextX + 300, Y, Tw + 150, Th - 30);
        selectButton.setBounds(TextX + 550, Y, Tw, Th - 30);
        resultLabel.setBounds(TextX + 300, Y + 10, Tw + 300, Th);
        uploadLabel.setBounds(TextX + 300, Y + 30, Tw + 300, Th);
        downloadLabel.setBounds(TextX + 300, Y + 50, Tw + 300, Th);
        page.add(uploadLabel);
        page.add(downloadLabel);

        page.add(interfaceComboBox);
        page.add(selectButton);
        page.add(resultLabel);
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
    }

    public static String formatSpeed(long speedInKbps) {
        if (speedInKbps < 1024) {
            return speedInKbps + " Kbps";
        } else if (speedInKbps < 1024 * 1024) {
            return String.format("%.2f Mbps", speedInKbps / 1024.0);
        } else if (speedInKbps < 1024 * 1024 * 1024) {
            return String.format("%.2f Gbps", speedInKbps / (1024.0 * 1024));
        } else {
            return String.format("%.2f Tbps", speedInKbps / (1024.0 * 1024 * 1024));
        }
    }

    private static NetworkIF findNetworkInterfaceByName(List<NetworkIF> networkIFs, String name) {
        for (NetworkIF networkIF : networkIFs) {
            if (networkIF.getDisplayName().equals(name)) {
                return networkIF;
            }
        }
        return null;
    }
    public static void addTimePage() {
        int TextX = 230;
        int Th = 50;
        int Tw = 100;
        int Y = 130;
        String filePath = "C:\\Users\\Administrator\\Desktop\\news.json";

        try {
            File file = new File(filePath);
            ObjectMapper objectMapper = new ObjectMapper();
            getJson json = objectMapper.readValue(file, getJson.class);

            // 解析日期
            LocalDate parsedDate = LocalDate.parse(json.getDate());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(I18nManager.getMessage("message.date"));
            String formattedDate = parsedDate.format(formatter);
            JLabel dateT = new JLabel(formattedDate);
            dateT.setBounds(TextX + 280, Y + 70, Tw + 300, Th);
            // 创建 JLabel 用于显示图片
            JLabel imageLabel = new JLabel();
            if(json.getNews().getImageURL()!=null)extracted(imageLabel,json.getNews().getImageURL(),json);
            int ImgW = json.getNews().getImageWidth();
            int ImgH = json.getNews().getImageHeight();

            JLabel title =new JLabel(json.getNews().getTitle());
            title.setBounds(TextX+280, Y + 250,  ImgW, ImgH);

            // 设置字体大小
            Font titleFont = new Font("Arial", Font.BOLD, 24); // 修改字体和大小
            title.setFont(titleFont);

            JLabel line1 = new JLabel(json.getNews().getLine1());
            line1.setBounds(TextX+280, Y + 250,  ImgW, ImgH);

            imageLabel.setBounds(TextX+280, Y + 110,  ImgW, ImgH);
            page.add(title);

            page.add(imageLabel);
            page.add(dateT);
            // 其他属性的访问方式类似

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extracted(JLabel imageLabel,String url,getJson json) {
        int ImgW = json.getNews().getImageWidth();
        int ImgH = json.getNews().getImageHeight();
        // 设置 JLabel 的大小
        imageLabel.setPreferredSize(new Dimension(ImgW, ImgH));

        try {
            // 创建 URL 对象
            URL imageUrl = new URL(url);

            // 使用 ImageIcon 从 URL 加载图片
            ImageIcon imageIcon = new ImageIcon(imageUrl);

            // 设置 JLabel 的图标
            imageLabel.setIcon(imageIcon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
