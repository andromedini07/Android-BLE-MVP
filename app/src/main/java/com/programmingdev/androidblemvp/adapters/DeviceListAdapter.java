package com.programmingdev.androidblemvp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programmingdev.androidblemvp.R;
import com.programmingdev.blecommmodule.BluetoothDeviceWrapper;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ItemViewHolder> {

    private List<BluetoothDeviceWrapper> deviceList;
    private OnItemButtonClickListener onItemButtonClickListener;

    public DeviceListAdapter(List<BluetoothDeviceWrapper> deviceList) {
        this.deviceList = deviceList;
        if (this.deviceList == null) {
            this.deviceList = new ArrayList<>();
        }
    }

    public DeviceListAdapter() {
        this.deviceList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble_device, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BluetoothDeviceWrapper bluetoothDeviceWrapper = deviceList.get(position);
        String deviceNameDisplay = "N/A";
        if (bluetoothDeviceWrapper.deviceName != null && !bluetoothDeviceWrapper.deviceName.isEmpty()) {
            deviceNameDisplay = bluetoothDeviceWrapper.deviceName;
        }

        holder.textViewDeviceName.setText(deviceNameDisplay);
        holder.textViewDeviceAddress.setText(bluetoothDeviceWrapper.deviceAddress);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public boolean addItem(BluetoothDeviceWrapper bluetoothDeviceWrapper) {
        if (bluetoothDeviceWrapper != null) {
            if (!deviceList.contains(bluetoothDeviceWrapper)) {
                deviceList.add(bluetoothDeviceWrapper);
                notifyItemInserted(deviceList.size());
                return true;
            }
        }
        return false;
    }

    public void updateItem(BluetoothDeviceWrapper bluetoothDeviceWrapper) {
        if (bluetoothDeviceWrapper != null) {
            if (deviceList.contains(bluetoothDeviceWrapper)) {
                int itemIndex = deviceList.indexOf(bluetoothDeviceWrapper);
                deviceList.set(itemIndex, bluetoothDeviceWrapper);
                notifyItemChanged(itemIndex);
            }
        }
    }

    public void updateItems(List<BluetoothDeviceWrapper> list) {
        if (list != null) {
            this.deviceList = list;
            notifyDataSetChanged();
        }
    }

    public void clear() {
        this.deviceList.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDeviceName, textViewDeviceAddress;
        Button buttonConnect;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDeviceName = itemView.findViewById(R.id.textViewDeviceName);
            textViewDeviceAddress = itemView.findViewById(R.id.textViewDeviceAddress);
            buttonConnect = itemView.findViewById(R.id.buttonConnect);

            buttonConnect.setOnClickListener(v -> {
                if (onItemButtonClickListener != null) {
                    BluetoothDeviceWrapper bluetoothDeviceWrapper = deviceList.get(getAdapterPosition());
                    onItemButtonClickListener.onItemButtonClicked(bluetoothDeviceWrapper, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener onItemButtonClickListener) {
        this.onItemButtonClickListener = onItemButtonClickListener;
    }

    public interface OnItemButtonClickListener {
        public void onItemButtonClicked(BluetoothDeviceWrapper bluetoothDeviceWrapper, int itemPosition);
    }
}