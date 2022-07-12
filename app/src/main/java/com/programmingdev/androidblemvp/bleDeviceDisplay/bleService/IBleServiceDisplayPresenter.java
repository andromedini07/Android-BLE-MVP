package com.programmingdev.androidblemvp.bleDeviceDisplay.bleService;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.models.BleServicesDisplay;

import java.util.List;

/**
 * The Blueprint of the Presenter used by BleServiceDisplayPresenter to get data from BleServiceDisplayFragment
 * and pass the results to the BleServiceDisplayFragment (View)
 */
public interface IBleServiceDisplayPresenter {
    List<BleServicesDisplay> getServiceDisplayList(List<BluetoothGattService> serviceList);
    void disconnectFromPeripheral(String deviceAddress);
    void requestMTU(String deviceAddress, int mtuSize);
    void destroy();
}