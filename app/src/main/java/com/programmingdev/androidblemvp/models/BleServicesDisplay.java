package com.programmingdev.androidblemvp.models;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model - Displays UUID, Name, and characteristics
 * Easier to manage and manipulate item properties in the RecyclerView
 */
public class BleServicesDisplay implements Serializable {
    public String uuid;
    public String name;
    public List<BleCharacteristicsDisplay> characteristicsDisplayList;

    public BleServicesDisplay(BluetoothGattService bluetoothGattService) {
        this.uuid = bluetoothGattService.getUuid().toString();
        characteristicsDisplayList = new ArrayList<>();
        List<BluetoothGattCharacteristic> characteristicList = bluetoothGattService.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristicList) {
            characteristicsDisplayList.add(new BleCharacteristicsDisplay(characteristic));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleServicesDisplay that = (BleServicesDisplay) o;
        return uuid.equalsIgnoreCase(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}