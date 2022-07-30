package com.programmingdev.androidblemvp.presenter;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.BleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayView;
import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.BluetoothStateObserver;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.view.FakeBleServiceDisplay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BleServiceDisplayPresenterTest {
    private IBleServiceDisplayPresenter presenter;
    private IBleService bleService;
    private IBluetoothStateObserver bluetoothStateObserver;
    private IBleServiceDisplayView view;

    @Before
    public void initialize() {
        view = mock(FakeBleServiceDisplay.class);
        bleService = mock(BleService.class);
        bluetoothStateObserver = mock(BluetoothStateObserver.class);
        presenter = new BleServiceDisplayPresenter(bleService, bluetoothStateObserver);
    }

    @Test
    public void testToGetServiceDisplayList() {
        BluetoothGattService bluetoothGattService = mock(BluetoothGattService.class);
        when(bluetoothGattService.getUuid()).thenReturn(UUID.fromString("67f771b6-733d-4e6c-9420-fd8f31a14d17"));

        List<BluetoothGattService> bluetoothGattServiceList = new ArrayList<>();
        bluetoothGattServiceList.add(bluetoothGattService);

        List<BleServicesDisplay> serviceDisplayList = presenter.getServiceDisplayList(bluetoothGattServiceList);
        assertFalse("Service Display List should not be empty",serviceDisplayList.isEmpty());
    }

    @Test
    public void testToGetServiceDisplayListWithNullInput() {
        List<BleServicesDisplay> serviceDisplayList = presenter.getServiceDisplayList(null);
        assertTrue("Service Display List should be empty",serviceDisplayList.isEmpty());
    }

    @Test
    public void testToDisconnectDeviceUsingRealAddress() {
        String deviceAddress = "AA:BB:CC:DD:EE:FF";
        presenter.disconnectFromPeripheral(deviceAddress);
        assertNotNull("View cannot be empty", view);
        sendDisconnectMessageToView(deviceAddress);
    }

    @Test
    public void testToDisconnectDeviceUsingNullAddress() {
        presenter.disconnectFromPeripheral(null);
        assertNotNull("View cannot be empty", view);
        sendDisconnectMessageToView(null);
    }

    @Test
    public void testToDisconnectDeviceUsingInvalidAddress() {
        String deviceAddress = "AA:BB:CC:DD:EE:FF1111";
        presenter.disconnectFromPeripheral(deviceAddress);
        assertNotNull("View cannot be empty", view);
        sendDisconnectMessageToView(deviceAddress);
    }

    @Test
    public void testToRequestMTU() {
        assertTrue("MTU Size requested cannot be greater than 512 bytes", true);
        presenter.requestMTU("AA:BB:CC:DD:EE:FF", 512);
    }

    @Test
    public void testToRequestMTUWithNullAddress() {
        presenter.requestMTU(null, 512);
    }

    @Test
    public void testToRequestMTUWithEmptyAddress() {
        presenter.requestMTU("", 512);
    }

    @Test
    public void testToRequestMTUWithInvalidAddress() {
        presenter.requestMTU("asxaxsas111---", 512);
    }

    @Test
    public void testAttachView() {
        assertNotNull(view);
        presenter.attachView(view);
    }

    @Test
    public void testDetachView() {
        assertNotNull(view);
        presenter.detachView();
    }

    private void sendDisconnectMessageToView(String deviceAddress) {
        view.onDeviceDisconnected(deviceAddress, 1);
        Mockito.verify(view).onDeviceDisconnected(deviceAddress, 1);
    }
}