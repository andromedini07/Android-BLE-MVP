package com.programmingdev.androidblemvp.di.modules;

import android.app.Application;

import com.programmingdev.androidblemvp.MyApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final MyApplication application;

    public ApplicationModule(MyApplication myApplication) {
        this.application = myApplication;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }
}