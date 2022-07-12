package com.programmingdev.androidblemvp.bleDeviceDisplay.bleService;

import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.programmingdev.androidblemvp.MyApplication;
import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.adapters.GattServiceListAdapter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.BleDeviceActivity;
import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;

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
    private BleDeviceActivity bleDeviceActivity;

    // Activity LifeCycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the activity
        if (getActivity() instanceof BleDeviceActivity) {
            bleDeviceActivity = (BleDeviceActivity) getActivity();
        }

        // Extract the Selected Device Address and the service list from the BleDeviceActivity
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedDeviceAddress = bundle.getString("SelectedDeviceAddress");
            serviceList = bundle.getParcelableArrayList("ServiceList");
        }

        // This callback will only be called when the BleServiceDisplayFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                // Disconnect from the Peripheral and exit the parent activity and fragment
                presenter.disconnectFromPeripheral(selectedDeviceAddress);
                if (bleDeviceActivity != null) {
                    bleDeviceActivity.finish();
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
        assert bleDeviceActivity != null;
        IDependencyService dependencyService = ((MyApplication) bleDeviceActivity.getApplication()).dependencyService;
        IBleService bleService = dependencyService.provideBLEService(getContext());
        IBluetoothStateObserver bluetoothStateObserver = new BluetoothStateObserver(getContext());
        presenter = dependencyService.providePresenter(this, bleService,bluetoothStateObserver);

        // Set the title and subtitle
        // Title - "Bluetooth GATT Services"
        // SubTitle - Selected Device MAC Address
        if (bleDeviceActivity != null) {
            bleDeviceActivity.setTitle("Bluetooth GATT Services");
            bleDeviceActivity.setSubtitle(selectedDeviceAddress);
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bleDeviceActivity.getApplicationContext(),
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
        if (bleDeviceActivity != null) {
            bleDeviceActivity.finish();
        }
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
        bleDeviceActivity.showAlertDialog(0, "Bluetooth Disabled", "Please Enable Bluetooth to continue scanning devices",
                (inputText, dialogCancelFlag) -> {
                    bleDeviceActivity.finish();
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