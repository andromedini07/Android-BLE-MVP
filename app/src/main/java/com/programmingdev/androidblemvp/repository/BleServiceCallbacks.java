package com.programmingdev.androidblemvp.repository;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;
import java.util.UUID;

public abstract class BleServiceCallbacks {
  public void onDeviceScanStarted(){}
  public void onDeviceScanStopped(){}
  public void onDeviceScanFailed(int errorCode, String message){}
  public void onDeviceScanTimeout(){}
  public void onDeviceScanResult(BluetoothDeviceWrapper bluetoothDeviceWrapper, byte[] scanRecordData){}
  public void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceWrapperList){}
  public void onDeviceDisconnected(String deviceAddress, int disconnectCode){}
  public void onDeviceConnectionFailed(String deviceAddress){}
  public void onBluetoothRestart(){}
  public void onCharacteristicWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data){}
  public void onCharacteristicUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data){}
  public void onCharacteristicWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] lastSentData, int errorCode){}
  public void onCharacteristicReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, int errorCode){}
  public void onDescriptorWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[]data){}
  public void onDescriptorUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data){}
  public void onDescriptorReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, int errorCode){}
  public void onDescriptorWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data, int errorCode){}
  public void onNotificationEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onIndicationEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onNotificationDisabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onNotificationsDisabled(String deviceAddress, UUID serviceUUID){}
  public void onServiceDiscoveryFailed(String deviceAddress, int code){}
  public void onNotificationNotSupported(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onIndicationNotSupported(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onEnablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onEnablingIndicationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
  public void onDisablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID){}
}