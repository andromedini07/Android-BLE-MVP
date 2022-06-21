package com.programmingdev.androidblemvp.models;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.programmingdev.blecommmodule.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BleCharacteristicsDisplay implements Serializable {
    public String uuid;
    public String name;
    public byte[] valueDisplay;
    public List<Property> properties;
    public List<BleDescriptorDisplay> descriptorDisplayList;
    public boolean isNotificationEnabled;

    public BleCharacteristicsDisplay(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.uuid = bluetoothGattCharacteristic.getUuid().toString();

        descriptorDisplayList = new ArrayList<>();

        properties = new ArrayList<>();
        int characteristicProperties = bluetoothGattCharacteristic.getProperties();

        if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            properties.add(Property.READ);
        }

        if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            properties.add(Property.WRITE);
        } else if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            properties.add(Property.WRITE_NO_RESPONSE);
        } else if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) > 0) {
            properties.add(Property.SIGNED_WRITE);
        }

        if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            properties.add(Property.INDICATE);
        } else if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            properties.add(Property.NOTIFY);
        }

        if ((characteristicProperties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) > 0) {
            properties.add(Property.EXTENDED_PROPS);
        }

        List<BluetoothGattDescriptor> descriptorList = bluetoothGattCharacteristic.getDescriptors();
        for (BluetoothGattDescriptor descriptor : descriptorList) {
            descriptorDisplayList.add(new BleDescriptorDisplay(descriptor));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleCharacteristicsDisplay that = (BleCharacteristicsDisplay) o;
        return uuid.equalsIgnoreCase(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}