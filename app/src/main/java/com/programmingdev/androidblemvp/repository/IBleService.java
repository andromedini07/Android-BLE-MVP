package com.programmingdev.androidblemvp.repository;

import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;
import java.util.UUID;

public interface IBleService {
    // Scanning Devices
    public void startDeviceScan();
    public void stopDeviceScan();

    // Connecting Devices
    public void connect(BluetoothDeviceWrapper bluetoothDeviceWrapper);
    public void connect(String deviceAddress);
    public void setMTU(String deviceAddress, int mtuSize);
    public void disconnect(String deviceAddress);
    public List<BluetoothDeviceWrapper> getConnectedDevices();
    public void disconnectAllDevices();
    public List<String> getConnectingDevicesList();
    public boolean isDeviceConnected(String deviceAddress);
    public boolean isDeviceScanningFailed();
    public boolean isScanning();

    public void readData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    public void readDescriptor(String deviceAddress,UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID);
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data);
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data, int characteristicWriteType);
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data);

    public void enableNotification(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    public void enableIndication(String deviceAddress, UUID serviceUUID, UUID characteristicUUID);
    public void disableNotification(String deviceAddress,UUID serviceUUID, UUID characteristicUUID);
    public void disableNotifications(String deviceAddress, UUID serviceUUID, List<UUID> characteristicUUIDList);

    public void registerBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks);
    public void unregisterBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks);
}