package com.programmingdev.androidblemvp.main;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.programmingdev.androidblemvp.MyApplication;
import com.programmingdev.androidblemvp.bleDeviceDisplay.BleDeviceActivity;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.BluetoothStateObserver;
import com.programmingdev.androidblemvp.repository.bluetoothStateObserver.IBluetoothStateObserver;
import com.programmingdev.androidblemvp.utils.BaseActivity;
import com.programmingdev.androidblemvp.adapters.DeviceListAdapter;
import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;
import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.utils.console;
import com.programmingdev.androidblemvp.location.LocationAccessPermissionCallback;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * The MainActivity is the component (implementation) of IMainView interface. The View can be mocked for unit testing.
 * It is the entry page of the application that takes care of
 * 1. Scanning for Bluetooth Devices.
 * 2. Displaying found devices in RecyclerView and allowing the user to connect to a Bluetooth Device.
 * 3. Connecting to a Bluetooth Device and once connected, navigates to the Next Activity(BleDeviceActivity).
 * <p>
 * Consists of event callbacks required to update the UI accordingly.
 * <p>
 * Communicates with the MainPresenter to perform an operation. The results are delivered to the MainActivity from the MainPresenter.
 * MainActivity --> MainPresenter[IMainPresenter]
 * MainActivity[IMainView] <-- MainPresenter[BleServiceCallbacks]
 */
public class MainActivity extends BaseActivity implements IMainView, LocationAccessPermissionCallback {

    private static final String TAG = "MainActivity";

    private IMainPresenter presenter;                                       // Presenter for the MainActivity
    private DeviceListAdapter deviceListAdapter;                            // List Adapter for displaying Bluetooth Devices found while scanning
    private SwipeRefreshLayout swipeRefreshLayout;                          // Layout used to refresh the list when user pulls to refresh
    private LinearLayout linearLayoutLabel, linearLayoutListContainer;      // Layouts for displaying RecyclerView and "Devices not found" TextView Respectively
    private View locationDisabledAlertView;                                 // An alert view that is displayed when location is disabled.
    private KProgressHUD hud;                                               // A custom progress dialog UI

    private BluetoothDeviceWrapper selectedDevice;                          // A Global variable assigned when user clicks on the list item

    // Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set action bar color programmatically
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.material_color_grey_primary)));
        }

        hud = KProgressHUD.create(this);

        // Todo Replace this with Data Binding
        // View ID mapping.
        locationDisabledAlertView = findViewById(R.id.linearLayoutLocationDisabledAlert);
        linearLayoutListContainer = findViewById(R.id.linearLayoutListContainer);
        linearLayoutLabel = findViewById(R.id.linearLayoutLabel);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Set the properties of SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#455A64"));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkCoarseLocation();
            deviceListAdapter.clear();
            showNoDevicesFoundLabel();
        });

        // Set the properties of RecyclerView
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        deviceListAdapter = new DeviceListAdapter();
        recyclerView.setAdapter(deviceListAdapter);

        // Add Item Divider between the RecyclerView Items
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        // Set the Callbacks for ItemButtonClick
        deviceListAdapter.setOnItemButtonClickListener((bluetoothDeviceWrapper, itemPosition) -> {
            selectedDevice = bluetoothDeviceWrapper;
            // When a "Connect" button of an item is clicked
            presenter.stopDeviceScan();
            presenter.connect(bluetoothDeviceWrapper);
            showProgressDialog("", "Connecting. Please wait...");
        });
    }

    /**
     * Activity Lifecycle - onStart
     * Instantiate the DependencyService Object and get the components required to instantiate the Presenter
     * Register LocationAccessPermissionCallback
     * Check the Coarse Location Permission
     * The Mobile app starts scanning for the Bluetooth Device when the Coarse/Fine Location permission as well as
     * the Mobile Location have been enabled by the user
     */
    @Override
    protected void onStart() {
        super.onStart();
        locationAccessPermissionCallback = this;

        IDependencyService dependencyService = ((MyApplication) getApplication()).dependencyService;
        IBleService bleService = dependencyService.provideBLEService(getApplicationContext());
        IBluetoothStateObserver bluetoothStateObserver = new BluetoothStateObserver(getApplicationContext());
        presenter = dependencyService.providePresenter(this, bleService, bluetoothStateObserver);

        checkCoarseLocation();
    }

    /**
     * Activity Lifecycle - onStop
     * Stop Scanning, Disconnect the device if connecting and release resources.
     * Unregister LocationAccessPermissionCallback
     */
    @Override
    protected void onStop() {
        super.onStop();
        deviceListAdapter.clear();
        if (!presenter.isScanning()) {
            presenter.stopDeviceScan();
        }

//        if (selectedDevice != null) {
//            if (!presenter.isDeviceConnectionInProgress()) {
//                presenter.disconnect(selectedDevice.getDeviceAddress());
//            }
//        }

        presenter.destroy();
        presenter = null;
        locationAccessPermissionCallback = null;
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates searching for Bluetooth Devices has been started.
     * <p>
     * Manipulate UI - Show Swipe Refresh Indicator
     */
    @Override
    public void onDeviceScanStarted() {
        console.log(TAG, "onDeviceScanStarted");
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates searching for Bluetooth Devices has been stopped.
     * <p>
     * Manipulate UI - Hide Swipe Refresh Indicator
     */
    @Override
    public void onDeviceScanStopped() {
        console.log(TAG, "onDeviceScanStopped");
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates searching for Bluetooth Devices has failed.
     *
     * @param errorCode - Scan failure error code
     * @param message   - Message to be displayed in UI view
     *                  <p>
     *                  Manipulate UI - Show "Scan Failed "Alert Dialog to the user.
     */
    @Override
    public void onDeviceScanFailed(int errorCode, String message) {
        console.log(TAG, "ErrorCode = " + errorCode + " Message = " + message);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (errorCode == 101) {
            showAlertDialog(0, "", message, null);
        }
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates searching for Bluetooth Devices has timed out.
     * <p>
     * Manipulate UI - Show Toast message to user.
     */
    @Override
    public void onDeviceScanTimeout() {
        console.log(TAG, "onDeviceScanTimeout");
        Toast.makeText(getApplicationContext(), "Device Scan Timeout", Toast.LENGTH_SHORT).show();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates that Bluetooth Device is found along with device information.
     *
     * @param bluetoothDeviceWrapper - The wrapper for BluetoothDevice object. This is useful to manipulate/update objects by
     *                               inserting into the list to be displayed in a ListView/RecyclerView.
     *                               <p>
     *                               Manipulate UI
     *                               Add/update the object to be displayed in a RecyclerView.
     *                               Hide Swipe refresh indicator if at least one Bluetooth Device is found.
     *                               Display the RecyclerView containing list of found Bluetooth Devices.
     */
    @Override
    public void onDeviceFound(BluetoothDeviceWrapper bluetoothDeviceWrapper) {
//        console.log(TAG, "onDeviceFound = " + new Gson().toJson(bluetoothDeviceWrapper));
        boolean stopRefresh = deviceListAdapter.addItem(bluetoothDeviceWrapper);
        if (stopRefresh) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                showListContainer();
            }
        }
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates that the mobile is disconnected from the Bluetooth Device. The Bluetooth Gatt connection is closed prior to invoking this callback.
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device the mobile is disconnected from.
     *                      <p>
     *                      Manipulate UI
     *                      Hide progress dialog (the device was connecting before).
     *                      Show Toast to user.
     *                      Start searching for Bluetooth Devices (By checking Coarse Location Permission).
     */
    @Override
    public void onDeviceDisconnected(String deviceAddress) {
        console.log(TAG, "onDeviceDisconnected");
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "Device Disconnected = " + deviceAddress, Toast.LENGTH_SHORT).show();
        checkCoarseLocation();
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates that connecting to the Bluetooth Device has failed.
     *
     * @param deviceAddress - The MAC Address of the Bluetooth Device.
     *                      <p>
     *                      Manipulate UI
     *                      Hide progress dialog (the device was connecting before).
     *                      Show Toast to user.
     *                      Start searching for Bluetooth Devices (By checking Coarse Location Permission).
     */
    @Override
    public void onDeviceConnectionFailed(String deviceAddress) {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "Device Connection Failed = " + deviceAddress, Toast.LENGTH_SHORT).show();
        checkCoarseLocation();
    }

    /**
     * Parent - IMainView (called by MainPresenter)
     * Indicates that the mobile is connected to the Bluetooth Device, its services and characteristics discovered and ready to communicate.
     *
     * @param deviceAddress - The MAC Address of the connected Bluetooth Device.
     * @param serviceList   - The Bluetooth Gatt Services supported by the Bluetooth Device.
     *                      <p>
     *                      Manipulate UI
     *                      Hide progress dialog (the device was connecting before).
     *                      Navigate to BleDeviceActivity with the selected device MAC address and Bluetooth Gatt services of the connected device through an intent.
     */
    @Override
    public void onDeviceConnectedAndReadyToCommunicate(String deviceAddress, List<BluetoothGattService> serviceList) {
        hideProgressDialog();

        // Pass selected device's MAC address and its services list to the next activity (BleDeviceActivity).
        // Navigate to the next activity by passing SelectedDeviceAddress and serviceArrayList through an Intent.
        ArrayList<BluetoothGattService> serviceArrayList = new ArrayList<>(serviceList);
        Intent intent = new Intent(getApplicationContext(), BleDeviceActivity.class);
        intent.putExtra("SelectedDeviceAddress", deviceAddress);
        intent.putParcelableArrayListExtra("ServiceList", serviceArrayList);
        startActivity(intent);
    }

    /**
     * Parent IMainView (called by MainPresenter)
     * Indicates the Bluetooth has been enabled by the user.
     * <p>
     * Manipulate UI
     * Start searching for Bluetooth Devices (By checking Coarse Location Permission).
     */
    @Override
    public void onBluetoothEnabled() {
        checkCoarseLocation();
    }

    /**
     * Parent IMainView (called by MainPresenter)
     * Indicates the Bluetooth has been disabled by the user.
     * <p>
     * Manipulate UI
     * Hide the progress dialog if the device connection is in progress.
     * Hide Swipe refresh indicator if the user pulls to refresh the list.
     * Stop scanning for the Bluetooth Devices if scanning is in progress.
     * Cancel the device connection if it is in progress.
     * Finally, show an alert dialog to the user.
     */
    @Override
    public void onBluetoothDisabled() {
        hideProgressDialog();
        swipeRefreshLayout.setRefreshing(false);

        if (presenter.isScanning()) {
            presenter.stopDeviceScan();
        }
        if (selectedDevice != null) {
            if (!presenter.isDeviceConnectionInProgress()) {
                // Some android phones do not disconnect from the peripheral when the Bluetooth is disabled
                presenter.disconnect(selectedDevice.getDeviceAddress());
            }
        }

        showAlertDialog(0, "Bluetooth Disabled", "Please Enable Bluetooth to continue scanning devices", null);
    }

    /**
     * Parent - LocationAccessPermissionCallback (called by BaseActivity)
     * Indicates the Coarse/Fine Location permission have been enabled by the user.
     * Start searching for Bluetooth Devices.
     */
    @Override
    public void onLocationAccessPermissionGranted() {
        if (!presenter.isScanning()) {
            presenter.startDeviceScan();
        }
    }

    /**
     * Parent - LocationAccessPermissionCallback (called by BaseActivity)
     * Indicates the Coarse/Fine Location permission have been denied by the user.
     */
    @Override
    public void onLocationAccessPermissionNotGranted() {

    }

    /**
     * Parent - LocationAccessPermissionCallback (called by BaseActivity)
     * Indicates the Mobile Device's location has been enabled by the user.
     * Manipulate UI - Hide alert view.
     */
    @Override
    public void onLocationEnabled() {
        locationDisabledAlertView.setVisibility(View.GONE);
    }

    /**
     * Parent - LocationAccessPermissionCallback (called by BaseActivity)
     * Indicates the Mobile Device's location has been disabled by the user.
     * Manipulate UI - Display alert on top of the view.
     */
    @Override
    public void onLocationDisabled() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        locationDisabledAlertView.setVisibility(View.VISIBLE);
    }

//--------------------Private Functions-----------------------------------------------------------------------

    /**
     * Method to display the Progress Dialog
     *
     * @param title   - Progress Dialog Title
     * @param message - Progress Dialog Text
     */
    private void showProgressDialog(String title, String message) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(false)
                .setCancellable(dialog -> {
                    dialog.dismiss();
                    onBackPressed();
                })
                .show();
    }

    /**
     * Method to hide the Progress Dialog
     */
    private void hideProgressDialog() {
        if (hud != null) {
            if (hud.isShowing()) {
                hud.dismiss();
            }
        }
    }

    /**
     * Method to show the RecyclerView when at least one Bluetooth Device is found and added to the RecyclerView
     * <p>
     * Manipulate UI
     * Show LinearLayout containing the RecyclerView.
     * Hide LinearLayout containing the TextView (No Devices Found).
     */
    private void showListContainer() {
        linearLayoutListContainer.setVisibility(View.VISIBLE);
        linearLayoutLabel.setVisibility(View.GONE);
    }

    /**
     * Method to show "No Devices Found" TextView when searching for the Bluetooth devices has been timed out and no devices are added in RecyclerView
     * <p>
     * Manipulate UI
     * Show Linear Layout containing the TextView (No Devices Found).
     * Hide Linear Layout containing the RecyclerView.
     */
    private void showNoDevicesFoundLabel() {
        linearLayoutListContainer.setVisibility(View.GONE);
        linearLayoutLabel.setVisibility(View.VISIBLE);
    }
//--------------------Private Functions-----------------------------------------------------------------------
}