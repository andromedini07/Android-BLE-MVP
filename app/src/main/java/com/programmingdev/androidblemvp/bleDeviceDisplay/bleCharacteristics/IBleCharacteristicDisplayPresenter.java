package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;
import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;

import java.util.List;

/**
 * The Blueprint of the Presenter used by BleCharacteristicDisplayPresenter to pass data to/from BleCharacteristicDisplayFragment and BleService
 */
public interface IBleCharacteristicDisplayPresenter {
    void requestMTU(String deviceAddress,int mtuSize);
    void enableNotification(String deviceAddress, String serviceUUID, String characteristicUUID);
    void enableIndication(String deviceAddress, String serviceUUID, String characteristicUUID);
    void disableNotification(String deviceAddress,String serviceUUID, String characteristicUUID);
    void readData(String deviceAddress, String serviceUUID, String characteristicUUID);
    void readDescriptor(String deviceAddress,String serviceUUID, String characteristicUUID, String descriptorUUID);
    void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data);
    void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data, int characteristicWriteType);
    void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data);
    void disableNotifications(String deviceAddress, String serviceUUID, List<BleCharacteristicsDisplay> characteristicsDisplayList);
    void disconnectFromPeripheral(String deviceAddress);
    void destroy();
}