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
import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;
import com.programmingdev.androidblemvp.models.BleDescriptorDisplay;
import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.utils.ByteUtils;
import com.programmingdev.androidblemvp.utils.console;

import java.util.List;

/**
 * The BleCharacteristicDisplayFragment displays the GATT Characteristics for the selected GATT Service of the Bluetooth Device
 * <p>
 * Communicates with the BleCharacteristicDisplayPresenter to perform an operation. The results are delivered to the
 * BleCharacteristicDisplayFragment from the BleCharacteristicDisplayPresenter.
 * <p>
 * BleCharacteristicDisplayFragment --> BleCharacteristicDisplayPresenter[IBleCharacteristicDisplayPresenter]
 * BleCharacteristicDisplayFragment[IBleCharacteristicDisplayFragmentView] <-- BleCharacteristicDisplayPresenter
 * BleCharacteristicDisplayFragment[IBleCharacteristicDisplayFragmentView] <-- BleCharacteristicDisplayPresenter[BleServiceCallbacks]
 * <p>
 * Note :
 * 1. The BleServiceDisplayPresenter receives the device's disconnect event from the BleServiceCallbacks
 * and forwards the event to the BleServiceDisplayFragment
 * 2. The BleCharacteristicDisplay and BleDescriptorDisplay model consists of characteristic name, characteristic uuid,
 * descriptor name and descriptor uuid as String properties that can be easily
 * added to the RecyclerView Adapter to display the list
 *
 * The Characteristics and Descriptors list are displayed in this format
 * Characteristic-1
 * Descriptor1-1
 * Descriptor1-2
 * Characteristic-2
 * Descriptor2-1
 * ...
 */
public class BleCharacteristicsDisplayFragment extends Fragment implements IBleCharacteristicDisplayView {
    private static final String TAG = "BleCharacteristicsDisplayFragment";
    private FragmentBleCharacteristicDisplayBinding binding;
    private BleServicesDisplay selectedBleServicesDisplay;
    private GattCharacteristicDisplayListAdapter adapter;
    private BleDeviceActivity activity;
    private IBleCharacteristicDisplayPresenter presenter;
    private String selectedDeviceAddress;

    // Activity LifeCycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the activity
        if (getActivity() instanceof BleDeviceActivity) {
            activity = (BleDeviceActivity) getActivity();
        }

        // Extract the Selected Device Address and the service list from the BleDeviceActivity
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

        // This callback will only be called when the BleCharacteristicFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                console.log(TAG, "onBackPressed");
                // Handle the back button event
                List<com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay> characteristicsDisplayList = adapter.getList();
                presenter.disableNotifications(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplayList);
                NavHostFragment.findNavController(BleCharacteristicsDisplayFragment.this).popBackStack();
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

        // Set the title and subtitle
        // Title - "Bluetooth GATT Characteristic"
        // SubTitle - UUID of the selected GATT Service
        if (activity != null) {
            activity.setTitle("Bluetooth GATT Characteristics");
            activity.setSubtitle(selectedBleServicesDisplay.uuid);
        }

        // Instantiate the DependencyService Object and get the components required to instantiate the Presenter
        IDependencyService dependencyService = new DependencyService();
        IBleService bleService = dependencyService.provideBLEService(getContext());
        presenter = dependencyService.providePresenter(this, bleService);

        // Set the properties of RecyclerView
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        // Instantiate the list adapter and set it to the RecyclerView
        adapter = new GattCharacteristicDisplayListAdapter();
        binding.recyclerView.setAdapter(adapter);

        // Add Divider Lines between the items in the RecyclerView
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(activity.getApplicationContext(), DividerItemDecoration.VERTICAL));
        //        ((SimpleItemAnimator) binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // Event Callback on RecyclerView item click
        adapter.setOnItemClickListener(new GattCharacteristicDisplayListAdapter.OnItemClickListener() {
            // Callback Fired when user clicks on Characteristic Item
            @Override
            public void onParentItemClicked(BleCharacteristicsDisplay characteristicsDisplay,
                                            int parentItemPosition, int code) {
                console.log(TAG, "Parent Item Position = " + parentItemPosition + "Bluetooth Characteristic UUID = "
                        + characteristicsDisplay.uuid);
                switch (code) {
                    case 1:    // Read
                        presenter.readData(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid);
                        break;
                    case 2:   // Write Request
                    case 3:   // Write Command
                        // Show DataConfigDialog to the user to enter data
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
                                    // Write Data to the Characteristic
                                    presenter.writeData(selectedDeviceAddress, selectedBleServicesDisplay.uuid, characteristicsDisplay.uuid, inputData);
                                    adapter.update(characteristicsDisplay.uuid, inputData);
                                }
                            }

                            @Override
                            public void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag) {
                                // Nothing
                            }
                        });
                        break;
                    case 4: // Enable Notification
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

            // Callback fired when user clicks on the descriptor
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

    /**
     * Activity Lifecycle - onStart
     * Extract the necessary GATT characteristics and GATT descriptors information from the BluetoothGattService objects for easy manipulation.
     * Populate the list in RecyclerView
     */
    @Override
    public void onStart() {
        super.onStart();
        List<BleCharacteristicsDisplay> wrapperList = selectedBleServicesDisplay.characteristicsDisplayList;
        if (wrapperList != null && !wrapperList.isEmpty()) {
            adapter.update(wrapperList);
            displayList();
        } else {
            displayNoCharacteristicsFoundMessage();
        }
    }

    /**
     * Activity Lifecycle - onDestroyView
     * Release resources.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.destroy();
        binding = null;
        presenter = null;
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     * <p>
     * Manipulate UI
     * Show Toast to user.
     * Exit the parent activity
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress, int code) {
        Toast.makeText(getContext(), "Device Disconnected = " + deviceAddress, Toast.LENGTH_SHORT).show();
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the notification is enabled for a GATT characteristic
     * <p>
     * Manipulate UI
     * Update the item in the RecyclerView (Show the text as Disable Notification in the button).
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onNotificationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Enabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, true);
        });
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the indication is enabled for a GATT characteristic
     * <p>
     * Manipulate UI
     * Update the item in the RecyclerView (Show the text as Disable Indication in the button).
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onIndicationEnabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Indication Enabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, true);
        });
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the indication is enabled for a GATT characteristic
     * <p>
     * Manipulate UI
     * Update the item in the RecyclerView (Show the text as Enable in the button).
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onNotificationDisabled(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Disabled = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        activity.runOnUiThread(() -> {
            adapter.update(characteristicUUID, false);
        });
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that enabling the notification for the GATT characteristic has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onNotificationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Notification Enabling Failed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        Toast.makeText(getContext(), "Enabling Notification Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that enabling the indication for the GATT characteristic has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onIndicationEnablingFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {
        console.log(TAG, "Indication Enabling Failed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID);
        Toast.makeText(getContext(), "Enabling Indication Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the GATT characteristic has been updated with data (Either from characteristic read or characteristic notification/indication).
     * <p>
     * Manipulate UI
     * Update value text in the item in RecyclerView
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onCharacteristicUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] data) {
        console.log(TAG, "OnCharacteristicUpdate = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID + " Data = " + ByteUtils.getHexStringFromByteArray(data, true));
        adapter.update(characteristicUUID, data);
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that writing data to the GATT characteristic has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onCharacteristicWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, byte[] lastSentData, int errorCode) {
        console.log(TAG, "OnCharacteristicWriteFailed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = " + characteristicUUID +
                " Last Sent Data = " + ByteUtils.getHexStringFromByteArray(lastSentData, true) + " Error Code = " + errorCode);
        Toast.makeText(getContext(), "Characteristic Write Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that reading data from the GATT characteristic has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onCharacteristicReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, int errorCode) {
        console.log(TAG, "OnCharacteristicReadFailed = " + deviceAddress + " Service UUID = " + serviceUUID + " Characteristic UUID = "
                + characteristicUUID + " ErrorCode = " + errorCode);
        Toast.makeText(getContext(), "Characteristic Read Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that the GATT Descriptor has been updated with data (descriptor read).
     * <p>
     * Manipulate UI
     * Update value text in the item in RecyclerView
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     * @param descriptorUUID - UUID of GATT Descriptor in String format.
     */
    @Override
    public void onDescriptorUpdate(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] data) {
        console.log(TAG, "OnDescriptorUpdate = " + deviceAddress + "Service UUID = " + serviceUUID + "Characteristic UUID = " + characteristicUUID + " Descriptor UUID = " + descriptorUUID + "Data = " + ByteUtils.getHexStringFromByteArray(data, true));
        adapter.update(characteristicUUID, descriptorUUID, data);
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that reading data from the GATT Descriptor has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     * @param descriptorUUID - UUID of GATT Descriptor in String format.
     */
    @Override
    public void onDescriptorReadFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, int errorCode) {
        console.log(TAG,"OnDescriptorReadFailed = "+ deviceAddress +" Service UUID = "+serviceUUID + " CharacteristicUUID = "+
                characteristicUUID + "DescriptorUUID = "+descriptorUUID + "Error Code = "+errorCode);
        Toast.makeText(getContext(), "Descriptor Read Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that writing data from the GATT Descriptor has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     * @param descriptorUUID - UUID of GATT Descriptor in String format.
     */
    @Override
    public void onDescriptorWriteFailed(String deviceAddress, String serviceUUID, String characteristicUUID, String descriptorUUID, byte[] lastSentData, int errorCode) {
        console.log(TAG,"OnDescriptorWriteFailed = "+ deviceAddress + " ServiceUUID = "+serviceUUID +" Characteristic UUID = "
                +characteristicUUID +" Descriptor UUID = "+descriptorUUID +" Last Sent Data = "+
                ByteUtils.getHexStringFromByteArray(lastSentData,false)+ " Error Code = "+errorCode);
        Toast.makeText(getContext(), "Descriptor Write Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent - IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates that disabling notification for the GATT Characteristic has failed.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress      - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param serviceUUID        - UUID of GATT Service in String format.
     * @param characteristicUUID - UUID of GATT Characteristic in String format.
     */
    @Override
    public void onDisablingNotificationFailed(String deviceAddress, String serviceUUID, String characteristicUUID) {
       console.log(TAG,"Disabling Notification Failed = "+ deviceAddress +" Service UUID = "+serviceUUID +" CharacteristicUUID = "+ characteristicUUID);
       Toast.makeText(getContext(),"Disabling Notification Failed",Toast.LENGTH_SHORT).show();
    }

    /**
     * Parent IBleCharacteristicDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates the Bluetooth has been disabled by the user.
     * <p>
     * Manipulate UI
     * Show an alert dialog to the user.
     * Exit the parent activity
     */
    @Override
    public void onBluetoothDisabled() {
        // Some android phones do not disconnect from the peripheral when the Bluetooth is disabled
        presenter.disconnectFromPeripheral(selectedDeviceAddress);
        activity.showAlertDialog(0, "Bluetooth Disabled", "Please Enable Bluetooth to continue scanning devices",
                (inputText, dialogCancelFlag) -> {
                    activity.finish();
                });
    }

//--------------------Private Functions-----------------------------------------------------------------------
    /**
     * Method to show the recyclerView when at least one BleServicesDisplay object is added into the RecyclerView
     */
    private void displayList() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.linearLayoutMessageIndicator.setVisibility(View.GONE);
    }

    /**
     * Method to show the message when the recyclerView is empty
     */
    private void displayNoCharacteristicsFoundMessage() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearLayoutMessageIndicator.setVisibility(View.VISIBLE);
    }
//--------------------Private Functions------------------------------------------------------------------------
}