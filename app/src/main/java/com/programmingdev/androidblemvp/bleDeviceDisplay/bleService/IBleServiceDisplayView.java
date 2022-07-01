package com.programmingdev.androidblemvp.bleDeviceDisplay.bleService;

/**
 * The Blueprint of the View used by BleServiceDisplayFragment that gets UI update from the presenter.
 */
public interface IBleServiceDisplayView {
    void onDeviceDisconnected(String deviceAddress, int code);
    void onBluetoothDisabled();
}