package ru.linachan.yggdrasil;

import ru.linachan.system_info.SystemInfo;
import ru.linachan.system_info.hardware.HardwareAbstractionLayer;
import ru.linachan.system_info.os.OperatingSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class YggdrasilAgent {

    private Properties cfg;

    private Logger logger = Logger.getLogger("Yggdrasil");

    private Boolean isRunningAgent = true;

    private YggdrasilDataStorage storage;
    
    private InetAddress serverHost = null;
    private Integer serverPort = 1488;
    
    private YggdrasilUDPService service;
    
    private DatagramSocket clientSocket;
    
    public YggdrasilAgent(String cfg) {
        this.configLogger();

        this.cfg = new Properties();
        try {
            this.cfg.load(new FileInputStream(cfg));
        } catch(IOException e) {
            this.logWarning("Config Error: " + e.getMessage());
        }

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            logWarning("Unable to open UDP socket");
        }

        service = new YggdrasilUDPService(this);
        
        String serverHost = this.getConfig("YggdrasilServerHost", null);
        if (serverHost != null) {
            try {
                this.serverHost = Inet4Address.getByName(serverHost);
            } catch (UnknownHostException e) {
                this.logWarning("Incorrect server host: " + serverHost);
            }
        }
        if (this.serverHost == null) {
            try {
                InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

                logInfo("Switching socket to broadcast mode...");
                clientSocket.setBroadcast(true);
                clientSocket.setSoTimeout(10);

                logInfo("Sending broadcast message...");
                sendData(new YggdrasilPacket((byte) 0xFF, (byte) 0xFF, new byte[8], null), broadcastAddress, serverPort);
                
                logInfo("Waiting for response...");
                YggdrasilPacket response = receiveData();
                
                if (response != null) {
                    this.serverHost = response.address;
                }
                
                logInfo("Switching socket to normal mode...");
                clientSocket.setBroadcast(false);
                clientSocket.setSoTimeout(0);
                
            } catch (SocketException e) {
                logWarning("Unable to broadcast via UDP: " + e.getMessage());
            } catch (UnknownHostException e) {
                logException(e);
            }
        }

        storage = new YggdrasilDataStorage(this, "YggdrasilAgent.storage");
    }

    private boolean sendData(YggdrasilPacket packet) {
        return sendData(packet, serverHost, serverPort);
    }
    
    private boolean sendData(YggdrasilPacket packet, InetAddress address, Integer port) {
        return service.sendData(clientSocket, packet, address, port);        
    }
    
    private YggdrasilPacket receiveData() {
        return service.recvData(clientSocket);
    }

    private void configLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new YggdrasilLogFormatter());

        for (Handler logHandler : this.logger.getHandlers()) {
            this.logger.removeHandler(logHandler);
        }
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(handler);
    }

    public String getConfig(String propertyName, String defaultValue) {
        return this.cfg.getProperty(propertyName, defaultValue);
    }

    public void logInfo(String message) {
        this.logger.info(message);
    }

    public void logWarning(String message) {
        this.logger.warning(message);
    }

    public void logException(Exception exc) {
        StackTraceElement[] stack_trace = exc.getStackTrace();
        this.logger.severe("##### " + exc.getClass().getSimpleName() + " #####");
        for (StackTraceElement line : stack_trace) {
            this.logger.severe("File " + line.getFileName() + " in method " + line.getMethodName() + " at line " + line.getLineNumber());
        }
        this.logger.severe("##### END OF TRACE #####");
    }

    private Map<String, String> getHostInformation() {
        Map<String, String> systemInfo = new HashMap<String, String>();

        SystemInfo info = new SystemInfo();
        OperatingSystem os = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();

        systemInfo.put("OS_NAME", os.getFamily());
        systemInfo.put("OS_VERSION", os.getVersion().toString());

        systemInfo.put("TOTAL_MEM", String.valueOf(hardware.getMemory().getTotal()));

        systemInfo.put("CPU_NAME", hardware.getProcessors()[0].getName());
        systemInfo.put("CPU_CORES", String.valueOf(hardware.getProcessors().length));

        return systemInfo;
    }

    public void processResponse(YggdrasilPacket response) {
        // TODO: Implement data processing
    }

    private void connectServer() throws YggdrasilException {
        if (serverHost != null) {
            this.logInfo("Trying to connect to server at: " + serverHost.getHostAddress());
            byte[] token = storage.getBinaryStorageValue("yggdrasilToken", new byte[8]);
            sendData(new YggdrasilPacket((byte)0x00, (byte) 0x00, token, getHostInformation()));
            YggdrasilPacket response = receiveData();
            if (!Arrays.equals(response.token, token)) {
                storage.putBinaryStorageValue("yggdrasilToken", response.token);
                logInfo("Setting authorization token [" + storage.getStorageValue("yggdrasilToken", "") + "]...");
            }
        } else {
            logWarning("Unable to locate Yggdrasil server.");
            throw new YggdrasilException("Unable to locate Yggdrasil server.");
        }
    }

    public void start() throws YggdrasilException, InterruptedException {
        connectServer();
        while (isRunningAgent) {
            byte[] token = storage.getBinaryStorageValue("yggdrasilToken", new byte[8]);
            sendData(new YggdrasilPacket((byte)0xF0, (byte)0x00, token, null));
            YggdrasilPacket response = receiveData();
            processResponse(response);
            Thread.sleep(5000);
        }
    }
}
