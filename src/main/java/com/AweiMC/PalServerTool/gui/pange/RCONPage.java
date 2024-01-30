package com.AweiMC.PalServerTool.gui.pange;

import com.AweiMC.PalServerTool.util.I18nManager;
import com.AweiMC.PalServerTool.util.RCON.ConnectServer;
import com.AweiMC.PalServerTool.util.RCON.RCONClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RCONPage {
    private static RCONClient rconClient;
    private static JTextArea logWindow;
    private static boolean login = false;
    private static String LIP;
    private static int Lport;
    private static String Lpassword;
    public static JPanel page =new JPanel(null); // 使用 null 布局
    private static void setLogWindow() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());

        logWindow = new JTextArea();
        logWindow.setEditable(false);  // 设置为不可编辑
        logWindow.setLineWrap(true);   // 自动换行

        // 将 JTextArea 放入 JScrollPane，以支持滚动
        JScrollPane scrollPane = new JScrollPane(logWindow);
        logPanel.add(scrollPane, BorderLayout.CENTER);


        logPanel.setBounds(450, 30, 450, 350);
        page.add(logPanel);
    }
    public enum logLevel {
        INFO,
        WARN,
        SERVER, ERROR,USER
    }
    public static void appendLevelText(String text,Enum<logLevel> level) {
        String Itext = "[" + level +"] " + text;
        appendText(Itext);
    }
    // 在 JTextArea 中追加文本
    public static void appendText(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String time = "[" + timestamp + "] ";
        SwingUtilities.invokeLater(() -> logWindow.append(time + text + "\n"));
    }

    public static void addPage() {
        setLogWindow();
        int textX = 10;
        int textY = 10;
        int textH = 20;
        int textW = 150;

        JButton connectButton = new JButton(I18nManager.getMessage("message.rcon.connect"));
        connectButton.setBounds(textX, textY, textW, textH);

        JButton disconnectButton = new JButton(I18nManager.getMessage("message.rcon.disconnect"));
        disconnectButton.setBounds(textX, textY + 22, textW, textH);


        JLabel IPText = new JLabel(I18nManager.getMessage("message.rcon.set.ip"));
        IPText.setBounds(textX+160, textY-2, textW, textH);

        JLabel CMDText = new JLabel(I18nManager.getMessage("message.rcon.cmd"));
        CMDText.setBounds(textX+640,textY-5,textW,textH);

        JLabel PRTText = new JLabel(I18nManager.getMessage("message.rcon.set.port"));
        PRTText.setBounds(textX+160,textY+20,textW-50,textH);
        JLabel PASText = new JLabel(I18nManager.getMessage("message.rcon.set.pas"));
        PASText.setBounds(textX+160,textY+40,textW-50,textH);

        JButton SENDM = new JButton(I18nManager.getMessage("message.rcon.conn.send"));
        SENDM.setBounds(textX,textY+350,textW-40,textH);

        JTextField SEND = new JTextField();
        SEND.setBounds(textX+ 120,textY+350,textW+140,textH);

        JTextField IP = new JTextField();
        IP.setBounds(textX + 250,textY-2,textW-80,textH);
        JTextField PORT = new JTextField();
        PORT.setBounds(textX + 250,textY+20,textW-80,textH);
        JTextField PAS = new JTextField();
        PAS.setBounds(textX + 250,textY+43,textW-80,textH);

        sendMessage(SENDM,SEND);
        page.add(CMDText);
        page.add(SEND);
        page.add(SENDM);
        page.add(PASText);
        page.add(PAS);
        page.add(PORT);
        page.add(PRTText);
        page.add(IP);
        page.add(IPText);

        //pange.add(connText);
        page.add(disconnectButton);
        page.add(connectButton);

        // 设置按钮的启用状态
        disconnectButton.setEnabled(!connectButton.isEnabled());

        // 为按钮添加点击事件
        connect(connectButton, disconnectButton,IP,PORT,PAS);
        disconnect(disconnectButton, connectButton,IP,PORT,PAS);
    }
    private static void sendMessage(JButton SENDM, JTextField SEND) {
        SENDM.addActionListener(e -> {
            String cmd = SEND.getText().trim();
            if (!cmd.isEmpty()) {
                if (login) {
                    String text = String.format(I18nManager.getMessage("message.rcon.send.cmd"),cmd);
                    appendLevelText(text,logLevel.USER);
                    rconClient = new RCONClient(LIP, Lport, Lpassword, cmd);
                    rconClient.call();
                    SEND.setText("");

                } else {
                    JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.rcon.conn.login"));
                }
            } else {
                JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.rcon.conn.empty"));
            }
        });
    }




    private static void connect(JButton connectButton, JButton disconnectButton, JTextField v1, JTextField v2, JTextField v3) {
        connectButton.addActionListener(e -> {
            // 启动连接操作的后台线程
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (v1.getText() != null && !v1.getText().isEmpty() && v2.getText() != null && !v2.getText().isEmpty()) {
                        try {
                            connectToRCON(connectButton, disconnectButton, v1.getText(), Integer.parseInt(v2.getText()), v3.getText());
                            login = true;
                        } catch (NumberFormatException ex) {
                            login = false;
                            JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.rcon.conn.invalid.port"));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.rcon.conn.error") + ex.getMessage());
                            login = false;
                        }
                    } else {
                        login=false;
                        JOptionPane.showMessageDialog(null, I18nManager.getMessage("message.rcon.conn.null"));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    if(!login) {
                        disconnectButton.setEnabled(false);
                        connectButton.setEnabled(true);
                        v1.setEnabled(true);
                        v2.setEnabled(true);
                        v3.setEnabled(true);
                    } else {
                        disconnectButton.setEnabled(true);
                        connectButton.setEnabled(false);
                        v1.setEnabled(false);
                        v2.setEnabled(false);
                        v3.setEnabled(false);
                    }
                }
            };

            worker.execute();
        });
    }


    private static void connectToRCON(JButton connectButton,JButton disconnectButton,String ip,int port,String password) {
        try {
            rconClient = new RCONClient(ip, port, password,"list");
            Lport = port;
            LIP = ip;
            Lpassword = password;
            int resultCode = rconClient.call();

            // 根据 resultCode 进行相应处理
            if (resultCode == 0) {
                System.out.println("Connection successful!");
                JOptionPane.showMessageDialog(null,I18nManager.getMessage("message.rcon.conn.sure"));
            } else {
                Lport =0;
                LIP = null;
                Lpassword = null;
                disconnectButton.setEnabled(false);
                connectButton.setEnabled(true);
                JOptionPane.showMessageDialog(null,I18nManager.getMessage("message.rcon.conn.error"));
            }
        } catch (Exception e) {
            Lport =0;
            LIP = null;
            Lpassword = null;
            System.err.println("Error during RCON connection: " + e.getMessage());
            JOptionPane.showMessageDialog(null,I18nManager.getMessage("message.rcon.conn.error.2")+e.getMessage());
            e.printStackTrace();
        }


    }

    private static void disconnect(JButton disconnectButton,JButton connectButton,JTextField v1,JTextField v2,JTextField v3) {
        disconnectButton.addActionListener(e -> {
            // 启动连接操作的后台线程
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws IOException {
                    // 在后台线程中执行断开连接的操作
                    ConnectServer.get().disconnect();
                    return null;
                }

                @Override
                protected void done() {
                    // 断开连接完成后更新Swing组件
                    login=false;
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    v1.setEnabled(true);
                    v2.setEnabled(true);
                    v3.setEnabled(true);

                }
            };

            worker.execute();
        });
    }

}
