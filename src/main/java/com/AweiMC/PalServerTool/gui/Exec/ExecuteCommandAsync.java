package com.AweiMC.PalServerTool.gui.Exec;

import com.AweiMC.PalServerTool.gui.pange.LocalServerPage;
import com.AweiMC.PalServerTool.util.I18nManager;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class ExecuteCommandAsync {

    public static void executeCommandAsyncWindows(List<String> command, JTextArea outputTextArea, JLabel jx) {
        Thread commandThread = new Thread(() -> {
            try {
                Process process = getProcess(command, outputTextArea);

                int exitCode = process.waitFor();
                System.out.println("Command executed with exit code: " + exitCode);
                outputTextArea.append(I18nManager.getMessage("message.install.out.code") +exitCode+ System.lineSeparator());
                SwingUtilities.invokeLater(() ->jx.setText(I18nManager.getMessage("message.install.done")));
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        commandThread.start();
    }


    private static Process getProcess(List<String> command, JTextArea outputTextArea) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            final String outputLine = line; // Final variable for use in lambda
            SwingUtilities.invokeLater(() -> {
                outputTextArea.append(outputLine + System.lineSeparator());
                System.out.println(outputLine);
                outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength()); // Scroll to end
            });
        }
        return process;
    }

    public static void executeCommandToCMD(List<String> command) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", String.join(" ", command));
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void executeCommandAsync(List<String> command, JTextArea outputTextArea) {
        Thread commandThread = new Thread(() -> {
            if (LocalServerPage.isAutoStart) {
                int exitCode = 0;
                try {
                    while (LocalServerPage.isAutoStart) {
                        Process less = getEProcess(command, outputTextArea);

                        if (exitCode != 0) {
                            String lf = I18nManager.getMessage("message.local.restart.2") + System.lineSeparator();
                            outputTextArea.append(lf);
                        }

                        exitCode = less.waitFor(); // 等待进程终止
                        if (getPidRun(less)) return;
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    exitCode = -1; // 或者其他适当的值
                    outputTextArea.append(I18nManager.getMessage("message.local.exit") + exitCode+System.lineSeparator());
                }

                System.out.println(exitCode);
            } else {
                try {
                    getEProcess(command, outputTextArea);
                    outputTextArea.append(I18nManager.getMessage("message.local.exit")+System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        commandThread.start();
    }
    private static boolean getPidRun(Process less) {
        long pid = less.toHandle().pid();
        Optional<ProcessHandle> processHandleOptional = ProcessHandle.of(pid);

        if (processHandleOptional.isPresent()) {
            ProcessHandle processHandle = processHandleOptional.get();
            if (processHandle.isAlive()) {
                System.out.println("进程正在运行，不执行操作");
                return true;
            } else {
                System.out.println("进程未运行，执行操作");
                return false;
                // 在这里执行启动服务器的代码
            }
        } else {
            System.out.println("获取不到 ProcessHandle，可能由于进程已经退出");
            return false;
        }
    }



    private static Process getEProcess(List<String> command, JTextArea outputTextArea) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            final String outputLine = line; // Final variable for use in lambda
            SwingUtilities.invokeLater(() -> {
                outputTextArea.append(outputLine + System.lineSeparator());
                System.out.println(outputLine);
                outputTextArea.setCaretPosition(outputTextArea.getDocument().getLength()); // Scroll to end
            });
        }

        return process;
    }


}
