package com.programmingdev.androidblemvp.dependencyService;

import android.content.Context;

import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

public interface IDependencyService {
    public IBleService provideBLEService(Context context);
    public IMainPresenter providePresenter(IMainView view, IBleService bleService);
    public IMainPresenter providePresenter(IMainView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver);
}
