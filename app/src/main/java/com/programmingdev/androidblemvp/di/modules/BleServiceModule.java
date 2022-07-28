package com.programmingdev.androidblemvp.di.modules;

import android.content.Context;

import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BleServiceModule {

    private Context context;

    public BleServiceModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public IBleService provideBleService() {
        return new BleService(context.getApplicationContext());
    }
}