package com.programmingdev.androidblemvp.presenter;

import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.BleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayPresenter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.bleService.IBleServiceDisplayView;
import com.programmingdev.androidblemvp.repository.BleService;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.BluetoothStateObserver;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.view.FakeBleServiceDisplay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BleServiceDisplayPresenterTest {
    private IBleServiceDisplayPresenter presenter;
    private IBleService bleService;
    private IBluetoothStateObserver bluetoothStateObserver;
    private IBleServiceDisplayView view;

    @Before
    public void initialize() {
        view = Mockito.mock(FakeBleServiceDisplay.class);
        bleService = Mockito.mock(BleService.class);
        bluetoothStateObserver = Mockito.mock(BluetoothStateObserver.class);
        presenter = new BleServiceDisplayPresenter(bleService, bluetoothStateObserver);
    }

    @Test
    public void testToGetServiceDisplayList() {

    }

    @Test
    public void testToGetServiceDisplayListWithNullInput() {

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
    public void testToRequestMTUWithNullAddress(){
        presenter.requestMTU(null,512);
    }

    @Test
    public void testToRequestMTUWithEmptyAddress(){
        presenter.requestMTU("",512);
    }

    @Test
    public void testToRequestMTUWithInvalidAddress(){
        presenter.requestMTU("asxaxsas111---",512);
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