package com.programmingdev.androidblemvp.repository.bluetoothStateObserver;

public interface IBluetoothStateObserver {
    public void register(IBluetoothStateObserverCallbacks observerCallbacks);
    public void unregister(IBluetoothStateObserverCallbacks observerCallbacks);

    public interface IBluetoothStateObserverCallbacks {
        void onBluetoothEnabled();
        void onBluetoothDisabled();
    }
}