package com.programmingdev.androidblemvp.bleDeviceDisplay.bleService;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.BleServiceCallbacks;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Component (implementation) of IBleServiceDisplayFragmentPresenter interface. The presenter can be mocked for unit testing.
 * The BleServiceDisplayPresenter is the controller layer that gets information from the BleServiceDisplayFragment (View), processes it and
 * returns to the BleServiceDisplayFragment (view).
 * <p>
 * The results from the BleService is forwarded to the View (BleServiceDisplayFragment) for UI update.
 * <p>
 * BleServiceDisplayFragment --->BleServiceDisplayPresenter[IBleServiceDisplayFragmentPresenter]
 * <p>
 * BleServiceDisplayFragment[IBleServiceDisplayFragmentView] <-- BleServiceDisplayPresenter[BleServiceCallbacks]
 * <p>
 * The Bluetooth Adapter States are also forwarded to the MainActivity from the presenter
 * BleServiceDisplayFragment[IBleServiceDisplayFragmentView] <-- BleServiceDisplayPresenter[IBluetoothStateObserverCallbacks]
 */

public class BleServiceDisplayPresenter extends BleServiceCallbacks implements IBleServiceDisplayPresenter, IBluetoothStateObserver.IBluetoothStateObserverCallbacks {

    private IBleServiceDisplayView view;
    private final IBleService bleService;
    private final IBluetoothStateObserver bluetoothStateObserver;   // Observer that reports Bluetooth Adapter State Changes. Bluetooth Enabling/Disabling states are reported via this observer

    @Inject
    public BleServiceDisplayPresenter(IBleService bleService, IBluetoothStateObserver bluetoothStateObserver) {
        this.bleService = bleService;
        this.bluetoothStateObserver = bluetoothStateObserver;
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     * <p>
     * Send to View(BleServiceDisplayFragment)
     *
     * @param deviceAddress  - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param disconnectCode - The Disconnect code.
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress, int disconnectCode) {
        // Send to View (BleServiceDisplayFragment)
        if (view != null) {
            view.onDeviceDisconnected(deviceAddress, disconnectCode);
        }
    }

    /**
     * Parent - IBleServiceDisplayFragmentPresenter (called from BleServiceDisplayFragment)
     * Converts objects from BluetoothGattService to BleServicesDisplay
     */
    @Override
    public List<BleServicesDisplay> getServiceDisplayList(List<BluetoothGattService> serviceList) {
        List<BleServicesDisplay> bleServicesDisplays = new ArrayList<>();
        if(serviceList!=null){
            for (BluetoothGattService service : serviceList) {
                bleServicesDisplays.add(new BleServicesDisplay(service));
            }
        }
        return bleServicesDisplays;
    }

    /**
     * Parent - IBleServiceDisplayFragmentPresenter (called from BleServiceDisplayFragment)
     * Disconnect from the peripheral
     */
    @Override
    public void disconnectFromPeripheral(String deviceAddress) {
        bleService.disconnect(deviceAddress);
    }

    /**
     * Parent - IBleCharacteristicDisplayPresenter (called from BleCharacteristicDisplayFragment)
     * Request MTU size from the Peripheral
     *
     * @param deviceAddress - MAC Address of theBluetooth device
     * @param mtuSize       - The MTU size to be set by the Central device
     */
    @Override
    public void requestMTU(String deviceAddress, int mtuSize) {
        if (bleService != null) {
            bleService.setMTU(deviceAddress, mtuSize);
        }
    }

    /**
     * Parent - BluetoothServiceCallbacks (called from BleService)
     * Indicates that the MTU is set to exchange data packets requested by the Central Device.
     * <p>
     * Send to View(BleCharacteristicDisplayFragment)
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param mtuSize       - The maximum size of the data the device can send to the peripheral in one shot.
     * @param status        - Status of setting the MTU size
     */
    @Override
    public void onMTUSet(String deviceAddress, int mtuSize, int status) {
        if (view != null) {
            view.onSetMTU(deviceAddress, mtuSize);
        }
    }

    @Override
    public void attachView(IBleServiceDisplayView view) {
        if (bleService != null) {
            bleService.registerBleServiceCallbacks(this);
        }

        if (bluetoothStateObserver != null) {
            bluetoothStateObserver.register(this);
        }

        this.view = view;
    }

    /**
     * Parent - IBleServiceDisplayFragmentPresenter (called from BleServiceDisplayFragment)
     * Destroys and release all resources when BleServiceDisplayFragment is not visible/destroyed.
     * Unregisters callbacks from BleService.
     * Unregisters callbacks from BluetoothStateObserver.
     * Disconnect view with presenter.
     */
    @Override
    public void detachView() {
        if (bleService != null) {
            bleService.unregisterBleServiceCallbacks(this);
        }

        if (bluetoothStateObserver != null) {
            bluetoothStateObserver.unregister(this);
        }

        view = null;
    }

    /**
     * Parent - IBluetoothStateObserver.IBluetoothStateObserverCallbacks
     * Indicates the Bluetooth has been enabled by the user.
     */
    @Override
    public void onBluetoothEnabled() {

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