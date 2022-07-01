package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;

import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;
import com.programmingdev.androidblemvp.repository.BleServiceCallbacks;
import com.programmingdev.androidblemvp.repository.IBleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleCharacteristicDisplayPresenter extends BleServiceCallbacks implements IBleCharacteristicDisplayPresenter {

    private IBleCharacteristicDisplayView view;
    private final IBleService bleService;

    public BleCharacteristicDisplayPresenter(IBleCharacteristicDisplayView view, IBleService bleService) {
        this.view = view;
        this.bleService = bleService;
        this.bleService.registerBleServiceCallbacks(this);
    }

    @Override
    public void destroy() {
        bleService.unregisterBleServiceCallbacks(this);
        view = null;
    }

    @Override
    public void enableNotification(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.enableNotification(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    @Override
    public void enableIndication(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.enableIndication(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    @Override
    public void disableNotification(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.disableNotification(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    @Override
    public void readData(String deviceAddress, String serviceUUID, String characteristicUUID) {
        if (bleService != null) {
            bleService.readData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID));
        }
    }

    @Override
    public void readDescriptor(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID) {
        if (bleService != null) {
            bleService.readDescriptor(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), UUID.fromString(descriptorUUID));
        }
    }

    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), data);
        }
    }

    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data, int characteristicWriteType) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), data, characteristicWriteType);
        }
    }

    @Override
    public void writeData(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data) {
        if (bleService != null) {
            bleService.writeData(deviceAddress, UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), UUID.fromString(descriptorUUID), data);
        }
    }

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
        if(view!=null){
            view.onNotificationsDisabled(deviceAddress,serviceUUID.toString());
        }
    }
}