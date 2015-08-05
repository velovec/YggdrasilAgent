package ru.linachan.yggdrasil;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class YggdrasilDataStorage {

    private YggdrasilAgent agent;

    private String storagePath;
    private byte[] MAGIC_STRING = "YggdrasilAgent#v1.0".getBytes();
    private Map<String, String> storageMap = new HashMap<String, String>();

    public YggdrasilDataStorage(YggdrasilAgent agent, String storagePath) {
        this.agent = agent;
        this.storagePath = storagePath;
        readStorageFile();
    }

    private Integer calculateMapSize(Map<String, String> map) {
        Integer totalSize = 0;
        if (map != null) {
            for (String key : map.keySet()) {
                totalSize += 8 + key.getBytes().length + map.get(key).getBytes().length;
            }
        }
        return totalSize;
    }

    private void readStorageFile() {
        File storageFile = new File(storagePath);
        if (storageFile.exists() && storageFile.isFile()) {
            try {
                FileInputStream storageStream = new FileInputStream(storageFile);
                byte[] magic_string = new byte[MAGIC_STRING.length];
                storageStream.read(magic_string);
                if (Arrays.equals(magic_string, MAGIC_STRING)) {
                    byte[] storageLengthByte = new byte[4];
                    storageStream.read(storageLengthByte);
                    Integer storageLength = ByteBuffer.wrap(storageLengthByte).getInt();
                    byte[] storageArray = new byte[storageLength];
                    storageStream.read(storageArray);
                    ByteBuffer storageBuffer = ByteBuffer.wrap(storageArray);
                    Integer storageBytesRead = 0;
                    while (storageBytesRead < storageLength) {
                        int keyLength = storageBuffer.getInt();
                        int valueLength = storageBuffer.getInt();

                        byte[] keyArray = new byte[keyLength];
                        byte[] valueArray = new byte[valueLength];

                        storageBuffer.get(keyArray);
                        storageBuffer.put(valueArray);

                        storageMap.put(new String(keyArray), new String(valueArray));
                        storageBytesRead += 8 + keyLength + valueLength;
                    }
                } else {
                    agent.logWarning("YggdrasilDataStorage: Incorrect storage file");
                }
            } catch (FileNotFoundException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to locate storage file: " + e.getMessage());
            } catch (IOException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to read storage file: " + e.getMessage());
            }
        } else {
            agent.logInfo("YggdrasilDataStorage: Initializing empty storage...");
            initializeEmptyStorageFile();
        }
    }

    private void writeStorageFile() {
        File storageFile = new File(storagePath);
        if (storageFile.exists() && storageFile.isFile()) {
            try {
                FileOutputStream storageStream = new FileOutputStream(storageFile);
                storageStream.write(MAGIC_STRING);

                Integer storageSize = calculateMapSize(storageMap);
                ByteBuffer writeBuffer = ByteBuffer.allocate(4 + storageSize);
                writeBuffer.putInt(storageSize);
                for (String key: storageMap.keySet()) {
                    byte[] keyArray = key.getBytes();
                    byte[] valueArray = storageMap.get(key).getBytes();

                    writeBuffer.putInt(keyArray.length);
                    writeBuffer.putInt(valueArray.length);

                    writeBuffer.put(keyArray);
                    writeBuffer.put(valueArray);
                }
                storageStream.write(writeBuffer.array());

                storageStream.close();
            } catch (FileNotFoundException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to locate storage at: " + e.getMessage());
            } catch (IOException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to write storage data to file: " + e.getMessage());
            }
        } else {
            agent.logWarning("YggdrasilDataStorage: Unable to write storage at: " + storagePath);
        }
    }

    private byte[] stringToByteArray(String string) {
        int len = string.length();
        byte[] resultArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            resultArray[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i+1), 16));
        }
        return resultArray;
    }

    private String byteArrayToString(byte[] byteArray) {
        String HEXES = "0123456789ABCDEF";
        String resultString = "";
        for (byte singleByte: byteArray) {
            resultString += HEXES.charAt((singleByte & 0xF0) >> 4);
            resultString += HEXES.charAt((singleByte & 0x0F));
        }
        return resultString;
    }

    public String getStorageValue(String key, String defaultValue) {
        if (storageMap.containsKey(key)) {
            return storageMap.get(key);
        }
        return defaultValue;
    }

    public boolean putStorageValue(String key, String value) {
        if (!storageMap.containsKey(key)) {
            storageMap.put(key, value);
            writeStorageFile();
            return true;
        }
        return false;
    }

    public byte[] getBinaryStorageValue(String key, byte[] defaultValue) {
        if (storageMap.containsKey(key)) {
            return stringToByteArray(storageMap.get(key));
        }
        return defaultValue;
    }

    public boolean putBinaryStorageValue(String key, byte[] value) {
        if (!storageMap.containsKey(key)) {
            storageMap.put(key, byteArrayToString(value));
            writeStorageFile();
            return true;
        }
        return false;
    }

    private void initializeEmptyStorageFile() {
        File storageFile = new File(storagePath);
        if (storageFile.exists() && storageFile.isFile()) {
            agent.logWarning("YggdrasilDataStorage: Unable to initialize storage at: " + storagePath);
        } else {
            try {
                FileOutputStream storageStream = new FileOutputStream(storageFile);
                storageStream.write(MAGIC_STRING);
                storageStream.close();
            } catch (FileNotFoundException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to locate storage at: " + e.getMessage());
            } catch (IOException e) {
                agent.logWarning("YggdrasilDataStorage: Unable to write storage data to file: " + e.getMessage());
            }
        }
    }
}
