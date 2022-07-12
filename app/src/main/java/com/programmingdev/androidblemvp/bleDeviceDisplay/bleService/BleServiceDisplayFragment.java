package com.programmingdev.androidblemvp.bleDeviceDisplay.bleService;

import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.programmingdev.androidblemvp.MyApplication;
import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.adapters.GattServiceListAdapter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.BleDeviceActivity;
import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;

import com.programmingdev.androidblemvp.dialogFragments.MTUConfigDialog;
import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.databinding.FragmentBleServiceDisplayBinding;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.BluetoothStateObserver;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.utils.console;

import java.util.ArrayList;
import java.util.List;

/**
 * The BleServiceDisplayFragment displays the GATT Services for the selected Bluetooth Device
 * <p>
 * Communicates with the BleServiceDisplayPresenter to perform an operation. The results are delivered to the
 * BleServiceDisplayFragment from the BleServiceDisplayPresenter.
 * <p>
 * BleServiceDisplayFragment --> BleServiceDisplayPresenter[IBleServiceDisplayPresenter]
 * BleServiceDisplayFragment[IBleServiceDisplayFragmentView] <-- BleServiceDisplayPresenter
 * BleServiceDisplayFragment[IBleServiceDisplayFragmentView] <-- BleServiceDisplayPresenter[BleServiceCallbacks]
 * <p>
 * Note :
 * 1. The BleServiceDisplayPresenter receives the device's disconnect event from the BleServiceCallbacks
 * and forwards the event to the BleServiceDisplayFragment
 * 2. The BleServicesDisplay model consists of service name, uuid and characteristics list as String properties that can be easily
 * added to the RecyclerView Adapter to display the list
 */
public class BleServiceDisplayFragment extends Fragment implements IBleServiceDisplayView {
    private static final String TAG = "BleServiceDisplayFragment";
    private FragmentBleServiceDisplayBinding binding;
    private String selectedDeviceAddress;
    private ArrayList<BluetoothGattService> serviceList;
    private GattServiceListAdapter adapter;
    private IBleServiceDisplayPresenter presenter;
    private BleDeviceActivity activity;

    // Activity LifeCycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the activity
        if (getActivity() instanceof BleDeviceActivity) {
            activity = (BleDeviceActivity) getActivity();
        }

        // Extract the Selected Device Address and the service list from the BleDeviceActivity
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedDeviceAddress = bundle.getString("SelectedDeviceAddress");
            serviceList = bundle.getParcelableArrayList("ServiceList");
        }

        // Set Menu
        setHasOptionsMenu(true);

        // This callback will only be called when the BleServiceDisplayFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                // Disconnect from the Peripheral and exit the parent activity and fragment
                presenter.disconnectFromPeripheral(selectedDeviceAddress);
                if (activity != null) {
                    activity.finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBleServiceDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate the DependencyService Object and get the components required to instantiate the Presenter
        assert activity != null;
        IDependencyService dependencyService = ((MyApplication) activity.getApplication()).dependencyService;
        IBleService bleService = dependencyService.provideBLEService(getContext());
        IBluetoothStateObserver bluetoothStateObserver = new BluetoothStateObserver(getContext());
        presenter = dependencyService.providePresenter(this, bleService,bluetoothStateObserver);

        // Set the title and subtitle
        // Title - "Bluetooth GATT Services"
        // SubTitle - Selected Device MAC Address
        if (activity != null) {
            activity.setTitle("Bluetooth GATT Services");
            activity.setSubtitle(selectedDeviceAddress);
        }

        // Set the properties of RecyclerView
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        // Instantiate the list adapter and set it to the RecyclerView
        adapter = new GattServiceListAdapter();
        binding.recyclerView.setAdapter(adapter);

        // Add Divider Lines between the items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity.getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        // Event Callback on RecyclerView item click
        adapter.setOnItemClickListener((bleServicesDisplay, itemPosition) -> {
            // Navigate to the BleCharacteristicDisplayFragment
            Bundle bundle = new Bundle();
            bundle.putString("SelectedDeviceAddress", selectedDeviceAddress);         // Pass the selected device MAC Address
            bundle.putSerializable("SelectedGattService", bleServicesDisplay);        // Pass the selected GATT Service
            NavHostFragment.findNavController(BleServiceDisplayFragment.this)
                    .navigate(R.id.action_BleServiceDisplayFragment_to_bleCharacteristicDisplayFragment, bundle);
        });
    }

    /**
     * Activity Lifecycle - onStart
     * Extract the necessary GATT services information from the BluetoothGattService objects for easy manipulation.
     * Populate the list in RecyclerView
     */
    @Override
    public void onStart() {
        super.onStart();
        List<BleServicesDisplay> bleServicesDisplays = presenter.getServiceDisplayList(serviceList);
        if (bleServicesDisplays != null && !bleServicesDisplays.isEmpty()) {
            displayServicesList();
        } else {
            displayNoServicesFoundMessage();
        }
        adapter.update(bleServicesDisplays);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.ble_device_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set_mtu) {
            if (presenter != null) {
                // Show DataConfigDialog to the user to enter the MTU size
                MTUConfigDialog dialogFragment = new MTUConfigDialog();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                dialogFragment.show(activity.getSupportFragmentManager(), "dialog");
                dialogFragment.setDialogListener(new MTUConfigDialog.DialogListener() {
                    @Override
                    public void onPositiveButtonClicked(int mtuSize, boolean dialogCancelFlag) {
                        // Request MTU to be set in the Peripheral
                        if (presenter != null) {
                            presenter.requestMTU(selectedDeviceAddress, mtuSize);
                        }
                    }

                    @Override
                    public void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag) {

                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Parent - IBleServiceDisplayView (called by BleServiceDisplayPresenter)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     *                      <p>
     *                      Manipulate UI*
     *                      Show Toast to user.
     *                      Exit the parent activity
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress, int code) {
        console.log(TAG, "onDeviceDisconnected");
        Toast.makeText(getContext(), "Device Disconnected = " + deviceAddress, Toast.LENGTH_SHORT).show();
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * Parent - IBleServiceDisplayView (called by BleCharacteristicDisplayPresenter)
     * Indicates the MTU size requested by the Bluetooth Central device is set.
     * <p>
     * Manipulate UI
     * Show Toast to user
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     * @param mtuSize       - The data size to be sent from the central to the peripheral in one shot
     */
    @Override
    public void onSetMTU(String deviceAddress, int mtuSize) {
        console.log(TAG, "onSetMTU = " + deviceAddress + " " + "MTU Size = " + mtuSize);
        activity.runOnUiThread(() -> {
            Toast.makeText(getContext(), "MTU size set to " + mtuSize, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Parent IBleServiceDisplayFragmentView (called by BleServiceDisplayPresenter)
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
    private void displayServicesList() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.linearLayoutMessageIndicator.setVisibility(View.GONE);
    }

    /**
     * Method to show the message when the recyclerView is empty
     */
    private void displayNoServicesFoundMessage() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearLayoutMessageIndicator.setVisibility(View.VISIBLE);
    }
//--------------------Private Functions-----------------------------------------------------------------------
}