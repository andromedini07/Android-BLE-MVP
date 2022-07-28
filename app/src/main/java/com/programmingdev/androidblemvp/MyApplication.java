package com.programmingdev.androidblemvp;

import android.app.Application;

import com.programmingdev.androidblemvp.di.components.ApplicationComponent;
import com.programmingdev.androidblemvp.di.components.DaggerApplicationComponent;
import com.programmingdev.androidblemvp.di.modules.ApplicationModule;
import com.programmingdev.androidblemvp.di.modules.BleServiceModule;
import com.programmingdev.androidblemvp.di.modules.BluetoothStateObserverModule;


// Todo Dependency Injection - Dagger 2
// Todo DataBinding
// Todo Code cleanup
// Todo Update SDK to Android S

// Custom Application class that needs to be specified
// in the AndroidManifest.xml file
public class MyApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .bleServiceModule(new BleServiceModule(this))
                .bluetoothStateObserverModule(new BluetoothStateObserverModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }
}