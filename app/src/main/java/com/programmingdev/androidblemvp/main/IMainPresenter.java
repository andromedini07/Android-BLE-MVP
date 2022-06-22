package com.programmingdev.androidblemvp.main;

import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

/**
 * The Blueprint of the Presenter used by MainPresenter to pass data to/from MainActivity and BleService
 */
public interface IMainPresenter {
    void startDeviceScan();
    void stopDeviceScan();
    void connect(String deviceAddress);
    void connect(BluetoothDeviceWrapper bluetoothDeviceWrapper);
    void disconnect(String deviceAddress);
    boolean isScanning();
    boolean isDeviceConnectionInProgress();
    void destroy();
}