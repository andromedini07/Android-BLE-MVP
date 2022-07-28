package com.programmingdev.androidblemvp.di.modules;

import android.content.Context;

import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.BluetoothStateObserver;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

import dagger.Module;
import dagger.Provides;

@Module
public class BluetoothStateObserverModule {

    private Context context;

    public BluetoothStateObserverModule(Context context){
        this.context = context;
    }

    @Provides
    public IBluetoothStateObserver provideBluetoothStateObserver(){
        return new BluetoothStateObserver(context.getApplicationContext());
    }
}