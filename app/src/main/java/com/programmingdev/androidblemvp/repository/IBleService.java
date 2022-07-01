package com.programmingdev.androidblemvp.repository;

import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;
import java.util.UUID;

public interface IBleService {
    // Scanning Devices
    void startDeviceScan();
    void stopDeviceScan();

    // Connecting Devices
    void connect(BluetoothDeviceWrapper bluetoothDeviceWrapper);
    void connect(String deviceAddress);
    void setMTU(String deviceAddress, int mtuSize);
    void disconnect(String deviceAddress);
    List<BluetoothDeviceWrapper> getConnectedDevices();
    void disconnectAllDevices();
    List<String> getConnectingDevicesList();
    boolean isDeviceConnected(String deviceAddress);
    boolean isDeviceScanningFailed();
    boolean isScanning();

    void readData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    void readDescriptor(String deviceAddress,UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID);
    void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data);
    void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data, int characteristicWriteType);
    void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data);

    void enableNotification(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    void enableIndication(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    void disableNotification(String deviceAddress,UUID serviceUUID, UUID characteristicUUID);
    void disableNotifications(String deviceAddress, UUID serviceUUID, List<UUID> characteristicUUIDList);

    void registerBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks);
    void unregisterBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks);
}