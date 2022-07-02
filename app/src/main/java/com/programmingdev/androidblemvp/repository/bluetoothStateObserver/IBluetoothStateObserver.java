package com.programmingdev.androidblemvp.repository.bluetoothStateObserver;

/**
 * The Blueprint of the BluetoothStateObserver
 */
public interface IBluetoothStateObserver {
    void register(IBluetoothStateObserverCallbacks observerCallbacks);
    void unregister(IBluetoothStateObserverCallbacks observerCallbacks);

    /**
     * Callbacks used to invoke when the Bluetooth is enabled/disabled by the user
     */
    interface IBluetoothStateObserverCallbacks {
        void onBluetoothEnabled();
        void onBluetoothDisabled();
    }
}