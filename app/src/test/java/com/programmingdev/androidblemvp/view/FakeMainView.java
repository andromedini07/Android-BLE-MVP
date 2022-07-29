package com.programmingdev.androidblemvp.view;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;

public class FakeMainView implements IMainView {
    @Override
    public void onDeviceScanStarted() {
       System.out.println("Scan Started");
    }

    @Override
    public void onDeviceScanStopped() {
       System.out.println("Scan Stopped");
    }

    @Override
    public void onDeviceScanFailed(int errorCode, String message) {

    }

    @Override
    public void onDeviceScanTimeout() {

    }

    @Override
    public void onDeviceFound(BluetoothDeviceWrapper bluetoothDeviceWrapper) {

    }

    @Override
    public void onDeviceDisconnected(String deviceAddress) {

    }

    @Override
    public void onDeviceConnectionFailed(String deviceAddress) {

    }

    @Override
    public void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceList) {

    }

    @Override
    public void onBluetoothEnabled() {

    }

    @Override
    public void onBluetoothDisabled() {

    }
}