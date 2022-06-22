package com.programmingdev.androidblemvp.bleDeviceDisplay;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.models.BleServicesDisplay;

import java.util.List;

public interface IBleServiceDisplayFragmentPresenter {
    public List<BleServicesDisplay> getServiceDisplayList(List<BluetoothGattService> serviceList);
    public void disconnectFromPeripheral(String deviceAddress);
    public void destroy();
}