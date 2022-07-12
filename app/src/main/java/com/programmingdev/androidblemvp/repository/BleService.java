package com.programmingdev.androidblemvp.repository;

import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.programmingdev.androidblemvp.utils.console;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;
import com.programmingdev.blecommmodule.Centralmodule.centralManager.BleCentralManagerFactory;
import com.programmingdev.blecommmodule.Centralmodule.centralManager.IBleCentralManager;
import com.programmingdev.blecommmodule.Centralmodule.interfaces.BleCentralManagerCallbacks;
import com.programmingdev.blecommmodule.ScannerModule.interfaces.BleScannerCallback;
import com.programmingdev.blecommmodule.ScannerModule.scanner.BleScanner;
import com.programmingdev.blecommmodule.ScannerModule.scanner.BleScannerFactory;
import com.programmingdev.blecommmodule.ScannerModule.scanner.IBleScanner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * The BleService is the component (implementation) of the IBleService that is responsible for communicating with the Bluetooth Device and
 * delivers the result to the presenter via callbacks.
 *
 * The IBleCentralManager is the blueprint of the Central Manager that takes care of connecting and communicating with the Bluetooth Device.
 * However, the implementation of this is a closed source.
 *
 * The IBleScanner is the blueprint of the BLE Scanner that takes care of scanning for Bluetooth Devices.
 * However, the implementation of this is the closed source.
 *
 * Presenter --> BleService[IBleService] --> [IBleCentralManager,IBleScanner]
 * Presenter[BleServiceCallbacks] <-- BleService[BleCentralManagerCallbacks, BleScannerCallbacks] <-- [BleCentralManager,BleScanner]
 *
 */
public class BleService extends BleCentralManagerCallbacks implements IBleService, BleScannerCallback {

    private static final String TAG = "BleService";

    private static BleService instance;
    private final IBleCentralManager bleCentralManager;
    private final IBleScanner bleScanner;
    private final List<BleServiceCallbacks> callbacksList;
    private Queue<UUID> characteristicUuidQueue;
    private boolean allNotificationsDisableInProgress;

    private BleService(Context context) {
        bleScanner = BleScannerFactory.provideInstance(context, BleScanner.class);
        bleCentralManager = BleCentralManagerFactory.provideInstance(context);
        callbacksList = new ArrayList<>();

        bleScanner.allowOperationsOnMainThread(true);
        bleScanner.setTimeoutInMs(30000);
//        bleScanner.addFilterDeviceService(Service_UUID);
//        bleScanner.addFilterDeviceMacAddress("43:DE:B2:90:63:5A");
        bleScanner.setOnBLEScanListener(this);

        bleCentralManager.setAllowOperationsOnUIThread(true);
        bleCentralManager.setRetryCount(4);
        bleCentralManager.setOnBluetoothCentralListener(this);
    }

    public static BleService getInstance(Context context) {
        if (instance == null) {
            instance = new BleService(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void startDeviceScan() {
        console.log(TAG, "startDeviceScan");
        bleScanner.startDeviceScan();
    }

    @Override
    public void stopDeviceScan() {
        bleScanner.stopDeviceScan();
    }

    @Override
    public void connect(BluetoothDeviceWrapper bluetoothDeviceWrapper) {
        bleCentralManager.connect(bluetoothDeviceWrapper);
    }

    @Override
    public void connect(String deviceAddress) {
        bleCentralManager.connect(deviceAddress);
    }

    @Override
    public void setMTU(String deviceAddress, int mtuSize) {
        bleCentralManager.setMTUSize(deviceAddress, mtuSize);
    }

    @Override
    public void disconnect(String deviceAddress) {
        bleCentralManager.disconnect(deviceAddress);
    }

    @Override
    public List<BluetoothDeviceWrapper> getConnectedDevices() {
        return bleCentralManager.getConnectedDevicesList();
    }

    @Override
    public void disconnectAllDevices() {

    }

    @Override
    public List<String> getConnectingDevicesList() {
        return bleCentralManager.getConnectingDevicesAddressList();
    }

    @Override
    public boolean isDeviceConnected(String deviceAddress) {
        return bleCentralManager.isDeviceConnected(deviceAddress);
    }

    @Override
    public boolean isDeviceScanningFailed() {
        return false;
    }

    @Override
    public boolean isScanning() {
        return bleScanner.isScanning();
    }

    @Override
    public void enableNotification(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        bleCentralManager.enableNotification(deviceAddress, serviceUUID, characteristicUUID);
    }

    @Override
    public void enableIndication(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        bleCentralManager.enableIndication(deviceAddress, serviceUUID, characteristicUUID);
    }

    @Override
    public void disableNotification(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        bleCentralManager.disableNotification(deviceAddress, serviceUUID, characteristicUUID);
    }

    @Override
    public void disableNotifications(String deviceAddress, UUID serviceUUID, List<UUID> characteristicUUIDList) {
        if (characteristicUUIDList != null) {
            characteristicUuidQueue = new ArrayDeque<>(characteristicUUIDList);
            if (!characteristicUuidQueue.isEmpty()) {
                UUID characteristicUUID = characteristicUuidQueue.poll();
                bleCentralManager.disableNotification(deviceAddress, serviceUUID, characteristicUUID);
            }
        }
    }

    @Override
    public void readData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        bleCentralManager.readData(deviceAddress, serviceUUID, characteristicUUID);
    }

    @Override
    public void readDescriptor(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID) {
        bleCentralManager.readData(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID);
    }

    @Override
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        bleCentralManager.sendData(deviceAddress, serviceUUID, characteristicUUID, data);
    }

    @Override
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data, int characteristicWriteType) {
        bleCentralManager.sendData(deviceAddress, serviceUUID, characteristicUUID, data, characteristicWriteType);
    }

    @Override
    public void writeData(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data) {
        bleCentralManager.sendData(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID, data);
    }

    @Override
    public void registerBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks) {
        if (bleServiceCallbacks != null) {
            callbacksList.add(bleServiceCallbacks);
        }
    }

    @Override
    public void unregisterBleServiceCallbacks(BleServiceCallbacks bleServiceCallbacks) {
        if (bleServiceCallbacks != null) {
            callbacksList.remove(bleServiceCallbacks);
        }
    }

    @Override
    public void onDeviceDisconnected(String deviceAddress, int disconnectCode) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceDisconnected(deviceAddress, disconnectCode);
        }
    }

    @Override
    public void onDeviceConnectionFailed(String deviceAddress) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceConnectionFailed(deviceAddress);
        }
    }

    @Override
    public void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceList) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceConnectedAndReadyToCommunicate(deviceAddress, serviceList);
        }
    }

    @Override
    public void onBluetoothRestart() {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onBluetoothRestart();
        }
    }

    @Override
    public void onCharacteristicWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onCharacteristicWrite(deviceAddress, serviceUUID, characteristicUUID, data);
        }
    }

    @Override
    public void onCharacteristicWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data, int errorCode) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onCharacteristicWriteFailed(deviceAddress, serviceUUID, characteristicUUID, data, errorCode);
        }
    }

    @Override
    public void onCharacteristicUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onCharacteristicUpdate(deviceAddress, serviceUUID, characteristicUUID, data);
        }
    }

    @Override
    public void onCharacteristicReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, int errorCode) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onCharacteristicReadFailed(deviceAddress, serviceUUID, characteristicUUID, errorCode);
        }
    }

    @Override
    public void onDescriptorWrite(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDescriptorWrite(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID, data);
        }
    }

    @Override
    public void onDescriptorWriteFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data, int errorCode) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDescriptorWriteFailed(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID, data, errorCode);
        }
    }

    @Override
    public void onDescriptorUpdate(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDescriptorUpdate(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID, data);
        }
    }

    @Override
    public void onDescriptorReadFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, int errorCode) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDescriptorReadFailed(deviceAddress, serviceUUID, characteristicUUID, descriptorUUID, errorCode);
        }
    }

    @Override
    public void onMTUSet(String deviceAddress, int mtuSize, int status) {
       for (BleServiceCallbacks callbacks : callbacksList){
           callbacks.onMTUSet(deviceAddress,mtuSize,status);
       }
    }

    @Override
    public void onServiceDiscoveryFailed(String deviceAddress, int code) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onServiceDiscoveryFailed(deviceAddress, code);
        }
    }

    @Override
    public void onNotificationNotSupported(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onNotificationNotSupported(deviceAddress, serviceUUID, characteristicUUID);
        }
    }

    @Override
    public void onNotificationEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onNotificationEnabled(deviceAddress, serviceUUID, characteristicUUID);
        }
    }

    @Override
    public void onIndicationsEnabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onIndicationEnabled(deviceAddress, serviceUUID, characteristicUUID);
        }
    }

    @Override
    public void onEnablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onEnablingNotificationFailed(deviceAddress, serviceUUID, characteristicUUID);
        }
    }

    @Override
    public void onEnablingIndicationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onEnablingIndicationFailed(deviceAddress, serviceUUID, characteristicUUID);
        }
    }

    @Override
    public void onNotificationDisabled(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (allNotificationsDisableInProgress) {
            if (!characteristicUuidQueue.isEmpty()) {
                UUID notificationCharacteristicUUID = characteristicUuidQueue.poll();
                bleCentralManager.disableNotification(deviceAddress, serviceUUID, notificationCharacteristicUUID);
            } else {
                allNotificationsDisableInProgress = false;
                for (BleServiceCallbacks callbacks : callbacksList) {
                    callbacks.onNotificationsDisabled(deviceAddress, serviceUUID);
                }
            }
        } else {
            for (BleServiceCallbacks callbacks : callbacksList) {
                callbacks.onNotificationDisabled(deviceAddress, serviceUUID, characteristicUUID);
            }
        }
    }

    @Override
    public void onDisablingNotificationFailed(String deviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if (allNotificationsDisableInProgress) {
            if (!characteristicUuidQueue.isEmpty()) {
                UUID notificationCharacteristicUUID = characteristicUuidQueue.poll();
                bleCentralManager.disableNotification(deviceAddress, serviceUUID, notificationCharacteristicUUID);
            } else {
                allNotificationsDisableInProgress = false;
                for (BleServiceCallbacks callbacks : callbacksList) {
                    callbacks.onNotificationsDisabled(deviceAddress, serviceUUID);
                }
            }
        } else {
            for (BleServiceCallbacks callbacks : callbacksList) {
                callbacks.onDisablingNotificationFailed(deviceAddress, serviceUUID, characteristicUUID);
            }
        }
    }

    @Override
    public void onStartScan() {
        console.log(TAG, "onStartScan");
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceScanStarted();
        }
    }

    @Override
    public void onStopScan() {
        console.log(TAG, "onStopScan");
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceScanStopped();
        }
    }

    @Override
    public void onScanFailed(int errorCode, String errorMessage) {
        console.log(TAG, "onScanFailed = > Error Code = " + errorCode + " Message = " + errorMessage);
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceScanFailed(errorCode, errorMessage);
        }
    }

    @Override
    public void onScanResult(BluetoothDeviceWrapper bluetoothDeviceWrapper, byte[] bytes) {
//        console.log(TAG, "Address = " + bluetoothDeviceWrapper.getBluetoothDevice().getAddress());
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceScanResult(bluetoothDeviceWrapper, bytes);
        }
    }

    @Override
    public void onScanTimeout() {
        for (BleServiceCallbacks callbacks : callbacksList) {
            callbacks.onDeviceScanTimeout();
        }
    }
}