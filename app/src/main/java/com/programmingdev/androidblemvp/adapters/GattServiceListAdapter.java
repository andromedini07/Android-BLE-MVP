package com.programmingdev.androidblemvp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.models.BleServicesDisplay;

import java.util.ArrayList;
import java.util.List;

public class GattServiceListAdapter extends RecyclerView.Adapter<GattServiceListAdapter.ItemViewHolder> {

    private List<BleServicesDisplay> serviceList;
    private OnItemClickListener itemClickListener;

    public GattServiceListAdapter(List<BleServicesDisplay> serviceList) {
        this.serviceList = serviceList;
        if (this.serviceList == null) {
            this.serviceList = new ArrayList<>();
        }
    }

    public GattServiceListAdapter() {
        this.serviceList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gatt_service, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BleServicesDisplay bleServicesDisplay = serviceList.get(position);
        String serviceName = "Unknown Service";
        if (bleServicesDisplay.name != null && !bleServicesDisplay.name.isEmpty()) {
            serviceName = bleServicesDisplay.name;
        }
        holder.textViewServiceName.setText(serviceName);
        holder.textViewServiceUUID.setText("UUID: " + bleServicesDisplay.uuid);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void update(List<BleServicesDisplay> bleServicesDisplays) {
        this.serviceList = bleServicesDisplays;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewServiceName, textViewServiceUUID;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewServiceUUID = itemView.findViewById(R.id.textViewServiceUID);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    BleServicesDisplay gattService = serviceList.get(getAdapterPosition());
                    itemClickListener.onItemClicked(gattService, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClicked(BleServicesDisplay bleServicesDisplay, int itemPosition);
    }
}