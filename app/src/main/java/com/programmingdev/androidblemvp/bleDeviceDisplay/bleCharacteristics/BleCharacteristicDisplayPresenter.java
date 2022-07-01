package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;

import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;
import com.programmingdev.androidblemvp.repository.BleServiceCallbacks;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Component (implementation) of IBleCharacteristicDisplayPresenter interface. The presenter can be mocked for unit testing.
 * The BleCharacteristicDisplayPresenter is the intermediate layer that passes information from the BleCharacteristicDisplayFragment (View)
 * to the BleService (Repository).
 * Takes the input from the view and forwards the request to the BleService (A communication repository).
 * Also, the results from the BleService is forwarded to the View (BleCharacteristicDisplay) for UI update.
 * <p>
 * BleCharacteristicDisplayFragment --->BleCharacteristicDisplayPresenter[IBleCharacteristicDisplayPresenter]---> BleService[IBleService]
 * <p>
 * BleCharacteristicDisplayFragment[IBleCharacteristicDisplayView] <-- BleCharacteristicPresenter[BleServiceCallbacks]<--- BleService
 * <p>
 * The Bluetooth Adapter States are forwarded to the BleCharacteristicDisplayFragment from the presenter
 * BleCharacteristicDisplayFragment[IBleCharacteristicDisplayView] <-- BleCharacteristicDisplayPresenter[IBluetoothStateObserverCallbacks]
 */
public class BleCharacteristicDisplayPresenter extends BleServiceCallbacks implements IBleCharacteristicDisplayPresenter, IBluetoothStateObserver.IBluetoothStateObserverCallbacks {

    private IBleCharacteristicDisplayView view;
    private final IBleService bleService;
    private IBluetoothStateObserver bluetoothStateObserver;

    public BleCharacteristicDisplayPresenter(IBleCharacteristicDisplayView view, IBleService bleService) {
        this.view = view;
        this.bleService = bleService;
        this.bluetoothStateObserver = null;
        this.bleService.registerBleServiceCallbacks(this);
    }

    public BleCharacteristicDisplayPresenter(IBleCharacteristicDisplayView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        this.view = view;
        this.bleService = bleService;
        this.bluetoothStateObserver = null;
        this.bleService.registerBleServiceCallbacks(this);
        this.bluetoothStateObserver.register(this);
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Enables Bluetooth GATT Characteristic Notification.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     */
    @Override
    public void enableNotification(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.enableNotification(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Enables Bluetooth GATT Characteristic Indication.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     */
    @Override
    public void enableIndication(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.enableIndication(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Disables Bluetooth GATT Characteristic Notification/Indication.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     */
    @Override
    public void disableNotification(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.disableNotification(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Reads data from Bluetooth GATT Characteristic.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     */
    @Override
    public void readData(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.readData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Reads data from Bluetooth GATT Descriptor.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     * @param descriptorUUID     - UUID of BLE GATT Descriptor in String format
     */
    @Override
    public void readDescriptor(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID) {
        if (bleService != null) {
            bleService.readDescriptor(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), UUID.fromString(descriptorUUID));
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Writes data to the Bluetooth GATT Characteristic.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     * @param data               - data to write
     *                           <p>
     *                           Note: By default - The write type for the characteristic is determined by the characteristic property defined by the BLE peripheral
     */
    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), data);
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Writes data to the Bluetooth GATT Characteristic.
     *
     * @param deviceAddress           - MAC Address of theBluetooth device
     * @param serviceUUID             - UUID of BLE GATT Service in String format
     * @param characteristicUUID      - UUID of BLE GATT Characteristic in String format
     * @param data                    - data to write
     * @param characteristicWriteType - The characteristic write type for the selected characteristic - Write Request (Write default) and Write Command
     *                                <p>
     *                                Note: For Write Request Type : The BLE central waits for the response from the BLE Peripheral.
     *                                The onDataWrite callback is invoked when the BLE peripheral responds to the write request
     *                                <p>
     *                                For Write Command Type : The BLE Central does not wait for the response from
     *                                the BLE Peripheral and onDataWrite callback is invoked immediately.
     */
    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data, int characteristicWriteType) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), data, characteristicWriteType);
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Writes data to the Bluetooth GATT Descriptor.
     *
     * @param deviceAddress      - MAC Address of theBluetooth device
     * @param serviceUUID        - UUID of BLE GATT Service in String format
     * @param characteristicUUID - UUID of BLE GATT Characteristic in String format
     * @param descriptorUUID     - UUID of BLE Gatt Descriptor in String format
     * @param data               - data to write
     */
    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), UUID.fromString(descriptorUUID), data);
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Disables Bluetooth Characteristic Notification for the characteristics.
     *
     * @param deviceAddress              - MAC Address of theBluetooth device
     * @param serviceUUID                - UUID of BLE GATT Service in String format
     * @param characteristicsDisplayList - List of Bluetooth GATT Characteristics
     */
    @Override
    public void disableNotifications(String deviceAddress, String serviceUUID, List<BleCharacteristicsDisplay> characteristicsDisplayList) {
        if (bleService != null) {
            List<UUID> notificationCharacteristicsList = new ArrayList<>();
            for (BleCharacteristicsDisplay bleCharacteristicsDisplay : characteristicsDisplayList) {
                if (bleCharacteristicsDisplay.isNotificationEnabled) {
                    notificationCharacteristicsList.add(UUID.fromString(bleCharacteristicsDisplay.uuid));
                }
            }

            bleService.disableNotifications(deviceAddress, UUID.fromString(serviceUUID), notificationCharacteristicsList);
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Destroys and release all resources when BleCharacteristicDisplayFragment is not visible/destroyed.
     * Unregisters callbacks from BleService.
     * Unregisters callbacks from BluetoothStateObserver.
     * Disconnect view with presenter.
     */
    @Override
    public void destroy() {
        bleService.unregisterBleServiceCallbacks(this);
        bluetoothStateObserver.unregister(this);
        view = null;
        bluetoothStateObserver = null;
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     *
     * Send to View(BleCharacteristicDisplayFragment)
     *
     * @param deviceAddress  - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param disconnectCode - The Disconnect code.
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress, int disconnectCode) {
        if (view != null) {
            view.onDeviceDisconnected(deviceAddress, disconnectCode);
        }
    }

    @Override
    public void onCharacteristicWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        if (view != null) {
            view.onCharacteristicWrite(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), data);
        }
    }

    @Override
    public void onCharacteristicUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        if (view != null) {
            view.onCharacteristicUpdate(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), data);
        }
    }

    @Override
    public void onCharacteristicWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] lastSentData, int errorCode) {
        if (view != null) {
            view.onCharacteristicWriteFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), lastSentData, errorCode);
        }
    }

    @Override
    public void onCharacteristicReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, int errorCode) {
        if (view != null) {
            view.onCharacteristicReadFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), errorCode);
        }
    }

    @Override
    public void onDescriptorWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data) {
        if (view != null) {
            view.onDescriptorWrite(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), descriptorUUID.toString(), data);
        }
    }

    @Override
    public void onDescriptorUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data) {
        if (view != null) {
            view.onDescriptorUpdate(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), descriptorUUID.toString(), data);
        }
    }

    @Override
    public void onDescriptorReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, int errorCode) {
        if (view != null) {
            view.onDescriptorUpdateFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), descriptorUUID.toString(), errorCode);
        }
    }

    @Override
    public void onDescriptorWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data, int errorCode) {
        if (view != null) {
            view.onDescriptorWriteFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString(), descriptorUUID.toString(), data, errorCode);
        }
    }

    @Override
    public void onNotificationEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        super.onNotificationEnabled(deviceAddress, serviceUUID, characteristicUUID);
        if (view != null) {
            view.onNotificationEnabled(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onIndicationEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
            view.onIndicationEnabled(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onNotificationDisabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
            view.onNotificationDisabled(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onServiceDiscoveryFailed(String deviceAddress, int code) {
        super.onServiceDiscoveryFailed(deviceAddress, code);
        if (view != null) {

        }
    }

    @Override
    public void onNotificationNotSupported(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {

        }
    }

    @Override
    public void onIndicationNotSupported(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
        }
    }

    @Override
    public void onEnablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
            view.onNotificationEnablingFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onEnablingIndicationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
            view.onIndicationEnablingFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onDisablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (view != null) {
            view.onDisablingNotificationFailed(deviceAddress, serviceUUID.toString(), characteristicUUID.toString());
        }
    }

    @Override
    public void onNotificationsDisabled(String deviceAddress, UUID serviceUUID) {
        if (view != null) {
            view.onNotificationsDisabled(deviceAddress, serviceUUID.toString());
        }
    }

    @Override
    public void onBluetoothEnabled() {

    }

    @Override
    public void onBluetoothDisabled() {

    }
}