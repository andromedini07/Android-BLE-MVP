package com.programmingdev.androidblemvp.dependencyService;

import android.content.Context;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayView;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayView;
import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

public interface IDependencyService {
    IBleService provideBLEService(Context context);
    IMainPresenter providePresenter(IMainView view, IBleService bleService);
    IMainPresenter providePresenter(IMainView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver);
    IBleServiceDisplayPresenter providePresenter(IBleServiceDisplayView view, IBleService bleService);
    IBleServiceDisplayPresenter providePresenter(IBleServiceDisplayView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver);
    IBleCharacteristicDisplayPresenter providePresenter(IBleCharacteristicDisplayView view, IBleService bleService);
    IBleCharacteristicDisplayPresenter providePresenter(IBleCharacteristicDisplayView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver);
}