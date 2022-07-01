package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;

public interface IBleCharacteristicDisplayFragmentView {
    public void onDeviceDisconnected(String deviceAddress, int code);
    public void onNotificationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onIndicationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onNotificationDisabled(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onNotificationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onIndicationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onCharacteristicUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data);
    public void onCharacteristicWrite(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data);
    public void onCharacteristicWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] lastSentData, int errorCode);
    public void onCharacteristicReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, int errorCode);
    public void onDescriptorUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data);
    public void onDescriptorUpdateFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, int errorCode);
    public void onDescriptorWrite(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data);
    public void onDescriptorWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] lastSentData, int errorCode);
    public void onDisablingNotificationFailed(String deviceAddress, String serviceUUID, String characteristicUUID);
    public void onNotificationsDisabled(String deviceAddress, String serviceUUID);
}