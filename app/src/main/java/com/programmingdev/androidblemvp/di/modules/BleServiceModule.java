package com.programmingdev.androidblemvp.di.modules;

import android.content.Context;

import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;

import dagger.Module;
import dagger.Provides;

@Module
public class BleServiceModule {

    private Context context;

    public BleServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    public IBleService provideBleService() {
        return BleService.getInstance(context.getApplicationContext());
    }
}