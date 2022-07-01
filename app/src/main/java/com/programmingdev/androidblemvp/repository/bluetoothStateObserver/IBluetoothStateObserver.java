package com.programmingdev.androidblemvp.repository.bluetoothStateObserver;

public interface IBluetoothStateObserver {
    void register(IBluetoothStateObserverCallbacks observerCallbacks);
    void unregister(IBluetoothStateObserverCallbacks observerCallbacks);

    interface IBluetoothStateObserverCallbacks {
        void onBluetoothEnabled();
        void onBluetoothDisabled();
    }
}