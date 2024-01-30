package com.AweiMC.PalServerTool.util.RCON;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ConnectServer implements AutoCloseable {
    private static ConnectServer instance;

    public static ConnectServer get() {
        if (instance == null) {
            instance = new ConnectServer();
        }
        return instance;
    }

    private Socket client;

    private ConnectServer() {
        client = new Socket();
    }

    public void connect(String hostname, int port) throws IOException {
        if (client.isConnected()) {
            throw new IllegalStateException("Client was already connected");
        }
        client.setKeepAlive(true);
        try {
            client.setTrafficClass(0x04);
        } catch (SocketException e) {
            // It's okay if we cannot set the traffic class
            System.err.println("Failed to set socket traffic class");
        }
        client.setSendBufferSize(1460);
        client.setReceiveBufferSize(4096);
        client.connect(new InetSocketAddress(hostname, port));
    }

    public void disconnect() throws IOException {
        client.close();
        client = new Socket();
    }
    public void close() throws IOException {
        if (client != null && !client.isClosed()) {
            disconnect();
        }
    }


    public int authenticate(String asciiPassword) throws IOException {
        if (client.isClosed() || !client.isConnected()) {
            throw new IllegalStateException("Tried to authenticate with a server before connecting to it!");
        }
        Packet p = new Packet(Packet.TYPE_LOGIN, asciiPassword.getBytes(StandardCharsets.US_ASCII));
        p.send();
        return p.getRequestID();
    }

    public int sendCommand(String command) throws IOException {
        if (client.isClosed() || !client.isConnected()) {
            throw new IllegalStateException("Tried to send a command to a server before connecting to it!");
        }

        Packet p = new Packet(Packet.TYPE_COMMAND, command.getBytes());
        p.send();
        return p.getRequestID();
    }



    public Packet parsePacket() throws IOException {
        Packet p = new Packet();
        byte[] in = new byte[4096];
        ByteBuffer buf = ByteBuffer.wrap(in);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int bytesRead = 0;
        while (bytesRead < 12) {
            buf.position(bytesRead);
            bytesRead += client.getInputStream().read(in, bytesRead, buf.remaining());
        }

        buf.rewind();
        p.length = buf.getInt();
        p.requestId = buf.getInt();
        p.type = buf.getInt();
        // Length includes requestId, type, and the two null bytes.
        // Subtract 10 to ignore those values from the payload size.
        p.payload = new byte[p.length - 10];

        buf.mark();
        while (bytesRead - 12 < p.payload.length + 2) {
            buf.position(bytesRead);
            bytesRead += client.getInputStream().read(in, bytesRead, buf.remaining());
        }
        buf.reset();
        buf.get(p.payload);
        return p;
    }
    public void requestPlayerInfo() throws IOException {
        if (client.isClosed() || !client.isConnected()) {
            throw new IllegalStateException("Tried to send a player info request to a server before connecting to it!");
        }

        Packet p = new Packet(Packet.TYPE_PLAYER_INFO_REQUEST, new byte[0]);
        p.send();
    }

    public class Packet {
        public static final int MAX_PACKET_SIZE = 1460;
        public static final int TYPE_LOGIN = 3;
        public static final int TYPE_COMMAND = 2;
        public static final int TYPE_CMD_RESPONSE = 0;
        public static final int TYPE_PLAYER_INFO_REQUEST = 4;
        public static final int TYPE_PLAYER_INFO_RESPONSE = 5;

        private int length, requestId, type;
        private byte[] payload;

        private Packet() {}




        private Packet(int type, byte[] payload) {
            length = 10 + payload.length;
            this.requestId = UUID.randomUUID().hashCode();
            while (this.requestId == -1) {
                // Request ID can't be -1
                this.requestId = UUID.randomUUID().hashCode();
            }
            this.type = type;
            this.payload = payload;
            if (length + 4 > MAX_PACKET_SIZE) {
                System.err.printf("Packet (ID #%d) size exceeds maximum packet size\n", requestId);
            }
        }


        private void send() throws IOException {
            byte[] out = new byte[length + 4];
            ByteBuffer buf = ByteBuffer.wrap(out);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(length).putInt(requestId).putInt(type).put(payload).putShort((short)0);
            client.getOutputStream().write(out);
        }

        public int getLength() {
            return length;
        }

        public int getRequestID() {
            return requestId;
        }

        public int getType() {
            return type;
        }

        public byte[] getRawData() {
            return payload;
        }
    }
}