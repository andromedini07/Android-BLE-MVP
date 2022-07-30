package com.programmingdev.androidblemvp.presenter;

import android.bluetooth.BluetoothGattService;

import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.view.FakeMainView;
import com.programmingdev.androidblemvp.main.IMainPresenter;
import com.programmingdev.androidblemvp.main.IMainView;
import com.programmingdev.androidblemvp.main.MainPresenter;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainPresenterTest {
    private IMainPresenter mainPresenter;

    private IMainView view;
    private BluetoothDeviceWrapper bluetoothDeviceWrapper;
    ArrayList<BluetoothGattService> sampleList;

    @Before
    public void setUp() {
//        MockitoAnnotations.initMocks(this);
        view = mock(FakeMainView.class);
        IBleService bleService = mock(IBleService.class);
        IBluetoothStateObserver bluetoothStateObserver = mock(IBluetoothStateObserver.class);
        bluetoothDeviceWrapper = mock(BluetoothDeviceWrapper.class);
        mainPresenter = new MainPresenter(bleService, bluetoothStateObserver);
        sampleList = mock(ArrayList.class);
    }

    @Test
    public void testToStartDeviceScan() {
        mainPresenter.startDeviceScan();
        assertNotNull(view);
        view.onDeviceScanStarted();
        verify(view, times(1)).onDeviceScanStarted();
    }

    @Test
    public void testToStopDeviceScan() {
        mainPresenter.stopDeviceScan();
        assertNotNull(view);
        view.onDeviceScanStopped();
        verify(view, times(1)).onDeviceScanStopped();
    }

    @Test
    public void testToConnectDeviceUsingNullWrapper() {
        mainPresenter.connect((BluetoothDeviceWrapper) null);
        // Mock to fire call back
        testToSendDeviceConnectedMessageToView();
    }

    @Test
    public void testToConnectDeviceUsingBluetoothWrapper() {
        assertNotNull(bluetoothDeviceWrapper);
        mainPresenter.connect(bluetoothDeviceWrapper);
        // Mock to fire call back
        testToSendDeviceConnectedMessageToView();
    }

    @Test
    public void testToConnectDeviceUsingDeviceAddress() {
        mainPresenter.connect("AA:BB:CC:DD:EE:FF");
        // Mock to fire call back
        testToSendDeviceConnectedMessageToView();
    }

    @Test
    public void testToConnectDeviceUsingEmptyString() {
        mainPresenter.connect("");
        testToSendDeviceConnectedMessageToView();
    }

    @Test
    public void testToConnectDeviceUsingInvalidAddress() {
        mainPresenter.connect("swwsw");
        testToSendDeviceDisconnectedMessageToView();
    }

    @Test
    public void testAttachView() {
        mainPresenter.attachView(view);
    }

    @Test
    public void testDetachView() {
        mainPresenter.detachView();
    }

    @Test
    public void testToSendDeviceConnectedMessageToView() {
        assertNotNull("View cannot be null", view);
        String deviceAddress = "AA:BB:CC:DD:EE:FF";
        view.onDeviceConnectedAndReadyToCommunicate(deviceAddress,sampleList);
        verify(view).onDeviceConnectedAndReadyToCommunicate(deviceAddress, sampleList);
    }

    @Test
    public void testToSendDeviceDisconnectedMessageToView() {
        assertNotNull("View cannot be null", view);
        String deviceAddress = "AA:BB:CC:DD:EE:FF";
        view.onDeviceDisconnected(deviceAddress);
        verify(view).onDeviceDisconnected(deviceAddress);
    }

    @Test
    public void testToDisconnectDeviceWithValidAddress() {
        String deviceAddress = "AA:BB:CC:DD:EE:FF";
        mainPresenter.disconnect(deviceAddress);
        testToSendDeviceDisconnectedMessageToView();
    }

    @Test
    public void testToDisconnectDeviceWithNullAddress() {
        mainPresenter.disconnect(null);
        testToSendDeviceDisconnectedMessageToView();
    }

    @Test
    public void testToDisconnectDeviceWithEmptyAddress() {
        mainPresenter.disconnect("");
        testToSendDeviceDisconnectedMessageToView();
    }

    @Test
    public void testToDisconnectDeviceWithInvalidAddress() {
        mainPresenter.disconnect("qssqs");
        testToSendDeviceDisconnectedMessageToView();
    }
}