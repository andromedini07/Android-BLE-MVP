package com.programmingdev.androidblemvp.main;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.repository.BleServiceCallbacks;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.List;

/**
 * Component (implementation) of IMainPresenter interface. The presenter can be mocked during unit testing.
 * The MainPresenter is the intermediate layer that passes information from the MainActivity (View) to the BleService (Repository).
 * Takes the input from the view and forwards the request to the BleService (A communication repository).
 * Also, the results from the BleService is forwarded to the View (MainActivity) for UI update.
 * <p>
 * MainActivity --->MainPresenter[IMainPresenter]---> BleService[IBleService]
 * <p>
 * MainActivity[IMainView] <-- MainPresenter[BleServiceCallbacks]<--- BleService
 *
 * The Bluetooth Adapter States are forwarded to the MainActivity from the presenter
 * MainActivity[IMainView] <-- MainPresenter[IBluetoothStateObserverCallbacks]
 */
public class MainPresenter extends BleServiceCallbacks implements IMainPresenter, IBluetoothStateObserver.IBluetoothStateObserverCallbacks {
    private static final String TAG = "MainPresenter";   // Tag for displaying Log Data

    private IMainView view;
    private final IBleService bleService;
    private IBluetoothStateObserver bluetoothStateObserver;   // Observer that reports Bluetooth Adapter State Changes. Bluetooth Enabling/Disabling states are reported via this observer

    public MainPresenter(IMainView view, IBleService bleService) {
        this.view = view;
        this.bleService = bleService;
        this.bleService.registerBleServiceCallbacks(this);
        this.bluetoothStateObserver = null;
    }

    public MainPresenter(IMainView view, IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        this.view = view;
        this.bleService = bleService;
        this.bluetoothStateObserver = bluetoothStateObserver;
        this.bluetoothStateObserver.register(this);
        this.bleService.registerBleServiceCallbacks(this);
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates searching for Bluetooth Devices has been started.
     * Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceScanStarted() {
        if (view != null) {
            view.onDeviceScanStarted();
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates searching for Bluetooth Devices has been stopped.
     * Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceScanStopped() {
        if (view != null) {
            view.onDeviceScanStopped();
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates searching for Bluetooth Devices has failed.
     *
     * @param errorCode - The code representing error
     * @param message   - The massage that can be displayed in the View (Activity/Fragment).
     *                  Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceScanFailed(int errorCode, String message) {
        if (view != null) {
            view.onDeviceScanFailed(errorCode, message);
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates searching for Bluetooth Devices has timed out.
     * Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceScanTimeout() {
        if (view != null) {
            view.onDeviceScanTimeout();
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that Bluetooth Device is found along with device information.
     *
     * @param bluetoothDeviceWrapper - The Wrapper of BluetoothDevice object. The Bluetooth Device name, MAC Address, etc.. can be obtained here.
     *                               The wrapper gives flexibility to add objects into the list to be represented in a ListView or RecyclerView
     * @param scanRecordData         - Contains Bluetooth Device's Advertisement Data
     *                               Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceScanResult(BluetoothDeviceWrapper bluetoothDeviceWrapper, byte[] scanRecordData) {
//        console.log(TAG, "Scan Data = " + ByteUtils.getHexStringFromByteArray(scanRecordData, true));
        if (view != null) {
            view.onDeviceFound(bluetoothDeviceWrapper);
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that the mobile is connected to the Bluetooth Device, its services and characteristics discovered and ready to communicate.
     *
     * @param deviceAddress - The MAC Address of the connected Bluetooth Device.
     * @param serviceList   - The Bluetooth Gatt Services supported by the Bluetooth Device.
     *                      Send to View (Activity/Fragment).
     */
    @Override
    public void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceList) {
        if (view != null) {
            view.onDeviceConnectedAndReadyToCommunicate(deviceAddress, serviceList);
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     *
     * @param deviceAddress  - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param disconnectCode - The Disconnect code.
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress, int disconnectCode) {
        if (view != null) {
            view.onDeviceDisconnected(deviceAddress);
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that connecting to the Bluetooth Device has failed.
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device.
     */
    @Override
    public void onDeviceConnectionFailed(String deviceAddress) {
        if (view != null) {
            view.onDeviceConnectionFailed(deviceAddress);
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Start scanning for Bluetooth Devices.
     */
    @Override
    public void startDeviceScan() {
        if (bleService != null) {
            bleService.startDeviceScan();
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Stop scanning for Bluetooth Devices.
     */
    @Override
    public void stopDeviceScan() {
        if (bleService != null) {
            if (bleService.isScanning()) {
                bleService.stopDeviceScan();
            }
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Connect to the Bluetooth Device.
     *
     * @param deviceAddress
     */
    @Override
    public void connect(String deviceAddress) {
        if (bleService != null) {
            bleService.connect(deviceAddress);
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Connect to the Bluetooth Device.
     *
     * @param bluetoothDeviceWrapper - Wrapper of BluetoothDevice object.
     */
    @Override
    public void connect(BluetoothDeviceWrapper bluetoothDeviceWrapper) {
        if (bleService != null) {
            bleService.connect(bluetoothDeviceWrapper);
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Disconnect from the Bluetooth Device.
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device
     */
    @Override
    public void disconnect(String deviceAddress) {
        if (bleService != null) {
            bleService.disconnect(deviceAddress);
        }
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Checks if scanning for Bluetooth Devices has been initiated.
     *
     * @return true - Scanning for Bluetooth Devices has been initiated.
     */
    @Override
    public boolean isScanning() {
        return bleService.isScanning();
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Checks if the device connection is already in progress.
     *
     * @return true - Device connection is already in progress.
     */
    @Override
    public boolean isDeviceConnectionInProgress() {
        List<String> connectingDevicesList = bleService.getConnectingDevicesList();
        return !connectingDevicesList.isEmpty();
    }

    /**
     * Parent - IMainPresenter (called from MainActivity)
     * Destroys and release all resources when MainActivity is not visible/destroyed.
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
     * Parent - IBluetoothStateObserver.IBluetoothStateObserverCallbacks
     * Indicates the Bluetooth has been enabled by the user.
     * Send to View (MainActivity).
     */
    @Override
    public void onBluetoothEnabled() {
        if (view != null) {
            view.onBluetoothEnabled();
        }
    }

    /**
     * Parent - IBluetoothStateObserver.IBluetoothStateObserverCallbacks
     * Indicates the Bluetooth has been disabled by the user.
     * Send to View (MainActivity).
     */
    @Override
    public void onBluetoothDisabled() {
        if (view != null) {
            view.onBluetoothDisabled();
        }
    }
}