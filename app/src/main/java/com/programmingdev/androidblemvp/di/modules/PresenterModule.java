package com.programmingdev.androidblemvp.di.modules;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.BleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.BleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.MainPresenter;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {
    @Provides
    public IMainPresenter provideMainPresenter(IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new MainPresenter(bleService, bluetoothStateObserver);
    }

    @Provides
    public IBleServiceDisplayPresenter provideBleServiceDisplayPresenter(IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new BleServiceDisplayPresenter(bleService, bluetoothStateObserver);
    }

    @Provides
    public IBleCharacteristicDisplayPresenter provideBleCharacteristicDisplayPresenter(IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new BleCharacteristicDisplayPresenter(bleService, bluetoothStateObserver);
    }
}