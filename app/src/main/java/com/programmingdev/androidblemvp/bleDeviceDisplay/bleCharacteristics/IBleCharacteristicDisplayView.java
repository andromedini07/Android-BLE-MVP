package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;

/**
 * The Blueprint of the View used by BleCharacteristicDisplayFragment that gets UI update from the presenter.
 */
public interface IBleCharacteristicDisplayView {
    void onDeviceDisconnected(String deviceAddress, int code);
    void onNotificationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onIndicationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onNotificationDisabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onNotificationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onIndicationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onCharacteristicUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data);
    void onCharacteristicWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] lastSentData, int errorCode);
    void onCharacteristicReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, int errorCode);
    void onDescriptorUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data);
    void onDescriptorReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, int errorCode);
    void onDescriptorWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] lastSentData, int errorCode);
    void onDisablingNotificationFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    void onSetMTU(String deviceAddress, int mtuSize);
    void onBluetoothDisabled();
}