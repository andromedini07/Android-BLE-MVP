package com.programmingdev.androidblemvp.main;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;

/**
 * The Blueprint of the View used by MainActivity that gets UI update from the presenter.
 */
public interface IMainView {
    void onDeviceScanStarted();
    void onDeviceScanStopped();
    void onDeviceScanFailed(int errorCode, String message);
    void onDeviceScanTimeout();
    void onDeviceFound(BluetoothDeviceWrapper bluetoothDeviceWrapper);

    void onDeviceDisconnected(String deviceAddress);
    void onDeviceConnectionFailed(String deviceAddress);
    void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceList);

    void onBluetoothEnabled();
    void onBluetoothDisabled();
}