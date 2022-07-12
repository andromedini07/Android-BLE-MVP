package com.programmingdev.androidblemvp.dependencyService;

import android.content.Context;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.BleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.BleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayView;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayView;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.androidblemvp.main.MainPresenter;
import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;

public final class DependencyService implements IDependencyService {

    @Override
    public IBleService provideBLEService(Context context) {
        return BleService.getInstance(context);
    }

    @Override
    public IMainPresenter providePresenter(IMainView view, IBleService bleService) {
        return new MainPresenter(view, bleService);
    }

    @Override
    public IMainPresenter providePresenter(IMainView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new MainPresenter(view, bleService, bluetoothStateObserver);
    }

    @Override
    public IBleServiceDisplayPresenter providePresenter(IBleServiceDisplayView view, IBleService bleService) {
        return new BleServiceDisplayPresenter(view, bleService);
    }

    @Override
    public IBleServiceDisplayPresenter providePresenter(IBleServiceDisplayView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new BleServiceDisplayPresenter(view, bleService, bluetoothStateObserver);
    }

    @Override
    public IBleCharacteristicDisplayPresenter providePresenter(IBleCharacteristicDisplayView view, IBleService bleService) {
        return new BleCharacteristicDisplayPresenter(view, bleService);
    }

    @Override
    public IBleCharacteristicDisplayPresenter providePresenter(IBleCharacteristicDisplayView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new BleCharacteristicDisplayPresenter(view, bleService, bluetoothStateObserver);
    }
}