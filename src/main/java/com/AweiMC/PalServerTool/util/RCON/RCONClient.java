package com.AweiMC.PalServerTool.util.RCON;

import com.AweiMC.PalServerTool.gui.pange.RCONPage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

public class RCONClient implements Callable<Integer> {
    private final String hostname;
    private final int port;
    private final String password;
    private final String command;  // 单个命令字符串

    private final ConnectServer api;

    private enum ExitCode {
        CONNECTION_ERROR(1),
        AUTH_FAILED(2);

        public final int code;

        ExitCode(final int code) {
            this.code = code;
        }
    }

    public RCONClient(String hostname, int port, String password, String commands) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
        this.command = commands;
        this.api = ConnectServer.get();
    }
    @Override
    public Integer call() {
        try {
            api.connect(hostname, port);
            System.out.printf("Connected to %s:%d\n", hostname, port);

            int reqId = api.authenticate(password);
            ConnectServer.Packet p = api.parsePacket();
            if (p.getRequestID() == -1 && p.getRequestID() != reqId) {
                System.err.println("Auth failed: Invalid passphrase");
                return ExitCode.AUTH_FAILED.code;
            }
            System.out.println("Authentication successful");

            // 在这里添加获取玩家信息的命令
            if (command != null && !command.isEmpty()) {
                Command(command);
            }
            return 0;
        } catch (IOException e) {
            System.err.printf("Failed to establish connection to %s:%d\n", hostname, port);
            e.printStackTrace();
            return ExitCode.CONNECTION_ERROR.code;
        } finally {
            try {
                api.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Command(String cmd) {
        try {
            int requestId = api.sendCommand(cmd);
            ConnectServer.Packet p = api.parsePacket();
            if (p.getRequestID() == requestId) {
                String resp = new String(p.getRawData(), StandardCharsets.US_ASCII);
                System.out.println(resp);
                RCONPage.appendLevelText(resp,RCONPage.logLevel.SERVER);
            }
        } catch (IOException e) {
            System.err.printf("Failed to send command: '%s'\n", cmd);
            System.err.printf(e.getMessage());
        }
    }

}
