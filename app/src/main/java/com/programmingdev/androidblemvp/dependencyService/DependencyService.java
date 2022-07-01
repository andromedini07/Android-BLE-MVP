package com.programmingdev.androidblemvp.dependencyService;

import android.content.Context;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.BleCharacteristicDisplayFragmentPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.BleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayFragmentPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics.IBleCharacteristicDisplayFragmentView;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayFragmentPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayFragmentView;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.androidblemvp.main.MainPresenter;
import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;

public class DependencyService implements IDependencyService{

    @Override
    public IBleService provideBLEService(Context context) {
        return BleService.getInstance(context);
    }

    @Override
    public IMainPresenter providePresenter(IMainView view, IBleService bleService) {
        return new MainPresenter(view,bleService);
    }

    @Override
    public IMainPresenter providePresenter(IMainView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        return new MainPresenter(view,bleService,bluetoothStateObserver);
    }

    @Override
    public IBleServiceDisplayFragmentPresenter providePresenter(IBleServiceDisplayFragmentView view, IBleService bleService) {
        return new BleServiceDisplayPresenter(view,bleService);
    }

    @Override
    public IBleCharacteristicDisplayFragmentPresenter providePresenter(IBleCharacteristicDisplayFragmentView view, IBleService bleService) {
        return new BleCharacteristicDisplayFragmentPresenter(view,bleService);
    }
}
