package com.programmingdev.androidblemvp.models;

import android.bluetooth.BluetoothGattDescriptor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model - Displays UUID, Name, and Values (Read/Write)
 * Easier to manage and manipulate item properties in the RecyclerView
 */
public class BleDescriptorDisplay implements Serializable {

    public String uuid;
    public String name;
    public byte[] valueDisplay;

    public BleDescriptorDisplay(BluetoothGattDescriptor bluetoothGattDescriptor) {
        this.uuid = bluetoothGattDescriptor.getUuid().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleDescriptorDisplay that = (BleDescriptorDisplay) o;
        return uuid.equalsIgnoreCase(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}