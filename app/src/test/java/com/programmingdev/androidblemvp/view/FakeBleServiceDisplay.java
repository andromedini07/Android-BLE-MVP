package com.programmingdev.androidblemvp.view;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayView;

public class FakeBleServiceDisplay implements IBleServiceDisplayView {
    @Override
    public void onDeviceDisconnected(String deviceAddress, int code) {

    }

    @Override
    public void onSetMTU(String deviceAddress, int mtuSize) {

    }

    @Override
    public void onBluetoothDisabled() {

    }
}