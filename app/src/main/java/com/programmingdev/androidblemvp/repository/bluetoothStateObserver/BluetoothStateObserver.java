package com.programmingdev.androidblemvp.repository.bluetoothStateObserver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothStateObserver implements IBluetoothStateObserver{
    private final Context context;
    private IBluetoothStateObserverCallbacks bluetoothStateObserverCallbacks;

    public BluetoothStateObserver(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void register(IBluetoothStateObserverCallbacks observerCallbacks) {
        this.bluetoothStateObserverCallbacks = observerCallbacks;
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(broadcastReceiver, filter1);
    }

    @Override
    public void unregister(IBluetoothStateObserverCallbacks observerCallbacks) {
        this.bluetoothStateObserverCallbacks = null;
        context.unregisterReceiver(broadcastReceiver);
    }

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