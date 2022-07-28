package com.programmingdev.androidblemvp.di.components;

import android.app.Application;

import com.programmingdev.androidblemvp.di.modules.ApplicationModule;
import com.programmingdev.androidblemvp.di.modules.BleServiceModule;
import com.programmingdev.androidblemvp.di.modules.BluetoothStateObserverModule;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

import javax.inject.Singleton;

import dagger.Component;

@Singleton()
@Component(modules = {ApplicationModule.class, BleServiceModule.class, BluetoothStateObserverModule.class})
public interface ApplicationComponent {
    Application getApplication();
    IBleService getBleService();
    IBluetoothStateObserver getBluetoothStateObserver();
}