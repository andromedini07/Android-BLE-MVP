package com.programmingdev.androidblemvp.bleDeviceDisplay;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.BleServiceCallbacks;
import com.programmingdev.androidblemvp.repository.IBleService;

import java.util.ArrayList;
import java.util.List;

public class BleServiceDisplayPresenter extends BleServiceCallbacks implements IBleServiceDisplayFragmentPresenter {

    private IBleServiceDisplayFragmentView view;
    private final IBleService bleService;

    public BleServiceDisplayPresenter(IBleServiceDisplayFragmentView view, IBleService bleService) {
        this.view = view;
        this.bleService = bleService;
        this.bleService.registerBleServiceCallbacks(this);
    }

    @Override
    public void onDeviceDisconnected(String deviceAddress, int disconnectCode) {
        if (view != null) {
            view.onDeviceDisconnected(deviceAddress, disconnectCode);
        }
    }

    @Override
    public List<BleServicesDisplay> getServiceDisplayList(List<BluetoothGattService> serviceList) {
        List<BleServicesDisplay> bleServicesDisplays = new ArrayList<>();
        for (BluetoothGattService service : serviceList) {
            bleServicesDisplays.add(new BleServicesDisplay(service));
        }
        return bleServicesDisplays;
    }

    @Override
    public void disconnectFromPeripheral(String deviceAddress) {
        bleService.disconnect(deviceAddress);
    }

    @Override
    public void destroy() {
        this.view = null;
        bleService.unregisterBleServiceCallbacks(this);
    }
}