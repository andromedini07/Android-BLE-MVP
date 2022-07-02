package com.programmingdev.androidblemvp.repository.bluetoothStateObserver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * The BluetoothStateObserver Class consists of a Broadcast Receiver that fires a callback based on Bluetooth state.
 * Fires callback when Bluetooth is enabled/disabled by the user.
 * The BluetoothStateObserver can be mocked for Unit Testing.
 *
 * Presenter/View[IBluetoothStateObserverCallbacks] <-- BluetoothStateObserver[BroadcastReceiver]
 */
public class BluetoothStateObserver implements IBluetoothStateObserver{
    private final Context context;
    private IBluetoothStateObserverCallbacks bluetoothStateObserverCallbacks;

    public BluetoothStateObserver(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Register Broadcast Receiver to get updates on Bluetooth State Changes
     * @param observerCallbacks - Callbacks fired when Bluetooth is enabled/disabled by the user
     */
    @Override
    public void register(IBluetoothStateObserverCallbacks observerCallbacks) {
        this.bluetoothStateObserverCallbacks = observerCallbacks;
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(broadcastReceiver, filter);
    }

    /**
     * Unregister Broadcast Receiver to stop getting updates on Bluetooth State Changes
     * @param observerCallbacks - Callbacks to cancel
     */
    @Override
    public void unregister(IBluetoothStateObserverCallbacks observerCallbacks) {
        this.bluetoothStateObserverCallbacks = null;
        context.unregisterReceiver(broadcastReceiver);
    }

    /**
     * Broadcast Receiver that detects and broadcasts changes in the Bluetooth State
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(bluetoothStateObserverCallbacks!=null){
                            bluetoothStateObserverCallbacks.onBluetoothDisabled();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if(bluetoothStateObserverCallbacks!=null){
                            bluetoothStateObserverCallbacks.onBluetoothEnabled();
                        }
                        break;
                }
            }
        }
    };
}