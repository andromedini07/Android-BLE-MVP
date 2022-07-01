package com.programmingdev.androidblemvp.bleDeviceDisplay.bleCharacteristics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.programmingdev.androidblemvp.databinding.FragmentBleCharacteristicDisplayBinding;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.gson.Gson;
import com.programmingdev.androidblemvp.adapters.GattCharacteristicDisplayListAdapter;
import com.programmingdev.androidblemvp.alertDialogFragments.DataConfigDialog;
import com.programmingdev.androidblemvp.bleDeviceDisplay.BleDeviceActivity;

import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;
import com.programmingdev.androidblemvp.models.BleDescriptorDisplay;
import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.utils.ByteUtils;
import com.programmingdev.androidblemvp.utils.console;

import java.util.List;

public class BleCharacteristicsDisplay extends Fragment implements IBleCharacteristicDisplayView {
    private static final String TAG = "BleCharacteristicsDisplayFragment";
    private FragmentBleCharacteristicDisplayBinding binding;
    private BleServicesDisplay selectedBleServicesDisplay;
    private GattCharacteristicDisplayListAdapter adapter;
    private BleDeviceActivity activity;
    private IBleCharacteristicDisplayPresenter presenter;
    private String selectedDeviceAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof BleDeviceActivity) {
            activity = (BleDeviceActivity) getActivity();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("SelectedGattService")) {
                selectedBleServicesDisplay = (BleServicesDisplay) bundle.getSerializable("SelectedGattService");
                console.log(TAG, "Selected Service Display = " + new Gson().toJson(selectedBleServicesDisplay));
            }
            if (bundle.containsKey("SelectedDeviceAddress")) {
                selectedDeviceAddress = bundle.getString("SelectedDeviceAddress");
            }
        }

        // This callback will only be called when the fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                console.log(TAG, "onBackPressed");
                // Handle the back button event
                List<com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay> characteristicsDisplayList = adapter.getList();
                presenter.disableNotifications(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplayList);
                NavHostFragment.findNavController(BleCharacteristicsDisplay.this).popBackStack();
                // navigateUp
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBleCharacteristicDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity != null) {
            activity.setTitle("Bluetooth GATT Characteristics");
            activity.setSubtitle(selectedBleServicesDisplay.uuid);
        }

        IDependencyService dependencyService = new DependencyService();
        IBleService bleService = dependencyService.provideBLEService(getContext());
        presenter = dependencyService.providePresenter(this, bleService);

        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GattCharacteristicDisplayListAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addItemDecoration(new DividerItemDecoration(activity.getApplicationContext(), DividerItemDecoration.VERTICAL));
        //        ((SimpleItemAnimator) binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter.setOnItemClickListener(new GattCharacteristicDisplayListAdapter.OnItemClickListener() {
            @Override
            public void onParentItemClicked(com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay characteristicsDisplay, int parentItemPosition, int code) {
                console.log(TAG, "Parent Item Position = " + parentItemPosition + "Bluetooth Characteristic UUID = " + characteristicsDisplay.uuid);
                switch (code) {
                    case 1:    // Read
                        presenter.readData(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        break;
                    case 2:   // Write
                    case 3:
                        DataConfigDialog dialogFragment = new DataConfigDialog();
                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        dialogFragment.show(activity.getSupportFragmentManager(), "dialog");
                        dialogFragment.setDialogListener(new DataConfigDialog.DialogListener() {
                            @Override
                            public void onPositiveButtonClicked(byte[] inputData, boolean dialogCancelFlag) {
                                if (inputData != null) {
                                    presenter.writeData(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid, inputData);
                                    adapter.update(characteristicsDisplay.uuid,inputData);
                                }
                            }

                            @Override
                            public void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag) {

                            }
                        });
                        break;
                    case 4:
                        if (!characteristicsDisplay.isNotificationEnabled) {
                            // Enable Notification
                            presenter.enableNotification(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        } else {
                            presenter.disableNotification(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        }
                        break;
                    case 5:  // Enable Indication
                        if (!characteristicsDisplay.isNotificationEnabled) {
                            presenter.enableIndication(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        } else {
                            presenter.disableNotification(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        }
                        break;
                }
            }

            @Override
            public void onChildItemClicked(BleDescriptorDisplay bleDescriptorDisplay, int childItemPosition, com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay bleCharacteristicsDisplay, int parentItemPosition, int code) {
                console.log(TAG, "Child Item Position = " + childItemPosition + "Bluetooth Descriptor UUID = " + bleDescriptorDisplay.uuid
                        + "Parent Item Position = " + parentItemPosition + "Bluetooth Characteristic UUID = " + bleCharacteristicsDisplay.uuid);
                switch (code) {
                    case 1: // Read
                        presenter.readDescriptor(selectedDeviceAddress, selectedBleServicesDisplay.uuid,
                                bleCharacteristicsDisplay.uuid, bleDescriptorDisplay.uuid);
                        break;
                    case 2: // Write
                        break;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        List<com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay> wrapperList = selectedBleServicesDisplay.characteristicsDisplayList;
        if (wrapperList != null && !wrapperList.isEmpty()) {
            adapter.update(wrapperList);
            displayList();
        } else {
            displayNoCharacteristicsFoundMessage();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        presenter.destroy();
        presenter = null;
    }

    private void displayList() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.linearLayoutMessageIndicator.setVisibility(View.GONE);
    }

    private void displayNoCharacteristicsFoundMessage() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearLayoutMessageIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeviceDisconnected(String deviceAddress, int code) {
        Toast.makeText(getContext(), "Device Disconnected = " + deviceAddress, Toast.LENGTH_SHORT).show();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onNotificationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Enabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, true);
        });
    }

    @Override
    public void onIndicationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Indication Enabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, true);
        });
    }

    @Override
    public void onNotificationDisabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Disabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, false);
        });
    }

    @Override
    public void onNotificationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Enabling Failed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
    }

    @Override
    public void onIndicationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Indication Enabling Failed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
    }

    @Override
    public void onCharacteristicUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data) {
        console.log(TAG, "OnCharacteristicUpdate = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID + " Data = " + ByteUtils.getHexStringFromByteArray(data, true));
        adapter.update(characteristicUUID, data);
    }

    @Override
    public void onCharacteristicWrite(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data) {

    }

    @Override
    public void onCharacteristicWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] lastSentData, int errorCode) {

    }

    @Override
    public void onCharacteristicReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, int errorCode) {

    }

    @Override
    public void onDescriptorUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data) {
        console.log(TAG, "OnDescriptorUpdate = " + deviceAddress + "Service UUID = " + serviceUUID + "Characteristic UUID = " + characteristicUUID + " Descriptor UUID = " + descriptorUUID + "Data = " + ByteUtils.getHexStringFromByteArray(data, true));
        adapter.update(characteristicUUID, descriptorUUID, data);
    }

    @Override
    public void onDescriptorUpdateFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, int errorCode) {

    }

    @Override
    public void onDescriptorWrite(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data) {

    }

    @Override
    public void onDescriptorWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] lastSentData, int errorCode) {

    }

    @Override
    public void onDisablingNotificationFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {

    }

    @Override
    public void onNotificationsDisabled(String deviceAddress, String serviceUUID) {
        console.log(TAG, "OnNotificationsDisabled = " + deviceAddress + "Service UUID = " + serviceUUID);
    }
}