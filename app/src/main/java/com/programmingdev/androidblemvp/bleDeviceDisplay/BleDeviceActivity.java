package com.programmingdev.androidblemvp.bleDeviceDisplay;

import android.os.Bundle;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.databinding.ActivityBleDeviceBinding;

import android.bluetooth.BluetoothGattService;

import androidx.appcompat.app.ActionBar;

import com.programmingdev.androidblemvp.utils.BaseActivity;
import com.programmingdev.androidblemvp.utils.console;

import java.util.ArrayList;

/**
 * The BleDeviceActivity has 2 fragments -
 * 1. BlServiceDisplayFragment
 * 2. BleCharacteristicDisplayFragment
 *
 * Basically, this activity receives the selected device address and GATT services list from the
 * previous activity (MainActivity) and forwards the same to to the BleServiceDisplay Fragment though an intent.
 */
public class BleDeviceActivity extends BaseActivity {
    private static final String TAG = "BleDeviceActivity";

    private AppBarConfiguration appBarConfiguration;
    private ActivityBleDeviceBinding binding;
    private String selectedDeviceAddress;
    private ArrayList<BluetoothGattService> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectedDeviceAddress = getIntent().getStringExtra("SelectedDeviceAddress");
        serviceList = getIntent().getParcelableArrayListExtra("ServiceList");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_ble_device);
        Bundle bundle = new Bundle();
        bundle.putString("SelectedDeviceAddress", selectedDeviceAddress);
        bundle.putParcelableArrayList("ServiceList", serviceList);
        // Pass the selected parameters received from the previous activity to the BleServiceDisplayFragment
        navController.setGraph(R.navigation.nav_graph, bundle);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_ble_device);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setSubtitle(String subTitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subTitle);
        }
    }
}