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

import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.adapters.GattServiceListAdapter;
import com.programmingdev.androidblemvp.bleDeviceDisplay.BleDeviceActivity;
import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;

import com.programmingdev.androidblemvp.models.BleServicesDisplay;
import com.programmingdev.androidblemvp.repository.IBleService;
import com.programmingdev.androidblemvp.databinding.FragmentBleServiceDisplayBinding;
import com.programmingdev.androidblemvp.utils.console;

import java.util.ArrayList;
import java.util.List;

/**
 * The BleServiceDisplayFragment displays the GATT Services for the selected Bluetooth Device
 */
public class BleServiceDisplayFragment extends Fragment implements IBleServiceDisplayFragmentView {
    private static final String TAG = "BleServiceDisplayFragment";
    private FragmentBleServiceDisplayBinding binding;
    private String selectedDeviceAddress;
    private ArrayList<BluetoothGattService> serviceList;
    private GattServiceListAdapter adapter;
    private IBleServiceDisplayFragmentPresenter presenter;
    private BleDeviceActivity bleDeviceActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof BleDeviceActivity) {
            bleDeviceActivity = (BleDeviceActivity) getActivity();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedDeviceAddress = bundle.getString("SelectedDeviceAddress");
            serviceList = bundle.getParcelableArrayList("ServiceList");

            for(BluetoothGattService service : serviceList){
                console.log(TAG,"Service = "+service.getUuid());
            }
        }

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
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

        if (bleDeviceActivity != null) {
            bleDeviceActivity.setTitle("Bluetooth GATT Services");
            bleDeviceActivity.setSubtitle(selectedDeviceAddress);
        }

        IDependencyService dependencyService = new DependencyService();
        IBleService bleService = dependencyService.provideBLEService(getContext());
        presenter = dependencyService.providePresenter(this, bleService);

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new GattServiceListAdapter();
        binding.recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setOnItemClickListener((bleServicesDisplay, itemPosition) -> {
            Bundle bundle = new Bundle();
            bundle.putString("SelectedDeviceAddress", selectedDeviceAddress);
            bundle.putSerializable("SelectedGattService", bleServicesDisplay);
            NavHostFragment.findNavController(BleServiceDisplayFragment.this)
                    .navigate(R.id.action_BleServiceDisplayFragment_to_bleCharacteristicDisplayFragment, bundle);
        });
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        presenter.destroy();
        presenter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDeviceDisconnected(String deviceAddress, int code) {
        console.log(TAG, "onDeviceDisconnected");
        Toast.makeText(getContext(), "Device Disconnected = " + deviceAddress, Toast.LENGTH_SHORT).show();
        if (bleDeviceActivity != null) {
            bleDeviceActivity.finish();
        }
    }

    private void displayServicesList() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.linearLayoutMessageIndicator.setVisibility(View.GONE);
    }

    private void displayNoServicesFoundMessage() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearLayoutMessageIndicator.setVisibility(View.VISIBLE);
    }
}