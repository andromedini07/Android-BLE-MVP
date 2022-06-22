package com.programmingdev.androidblemvp.bleDeviceDisplay;
import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;

import java.util.List;

public interface IBleCharacteristicDisplayFragmentPresenter {
    public void destroy();
    public void enableNotification(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void enableIndication(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void disableNotification(String deviceAddress,String serviceUUID, String characteristicUUID);
    public void readData(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void readDescriptor(String deviceAddress,String serviceUUID, String characteristicUUID, String descriptorUUID);
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data);
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data, int characteristicWriteType);
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data);
    public void disableNotifications(String deviceAddress, String serviceUUID, List<BleCharacteristicsDisplay> characteristicsDisplayList);
}