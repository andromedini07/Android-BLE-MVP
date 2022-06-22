package com.programmingdev.androidblemvp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.models.BleDescriptorDisplay;
import com.programmingdev.androidblemvp.utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GattDescriptorDisplayListAdapter extends RecyclerView.Adapter<GattDescriptorDisplayListAdapter.ItemViewHolder> {

    private List<BleDescriptorDisplay> descriptorDisplayList;
    private OnChildItemClickListener onChildItemClickListener;

    public GattDescriptorDisplayListAdapter(List<BleDescriptorDisplay> descriptorDisplayList) {
        this.descriptorDisplayList = descriptorDisplayList;
        if (this.descriptorDisplayList == null) {
            this.descriptorDisplayList = new ArrayList<>();
        }
    }

    public GattDescriptorDisplayListAdapter() {
        this.descriptorDisplayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gatt_descriptors, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BleDescriptorDisplay bleDescriptorDisplay = descriptorDisplayList.get(position);
        String descriptorName = "Unknown Descriptor";
        if (bleDescriptorDisplay.name != null && !bleDescriptorDisplay.name.isEmpty()) {
            descriptorName = bleDescriptorDisplay.name;
        }
        holder.textViewDescriptorName.setText(descriptorName);
        holder.textViewDescriptorUUID.setText(bleDescriptorDisplay.uuid);

        if (bleDescriptorDisplay.uuid.equalsIgnoreCase("00002902-0000-1000-8000-00805f9b34fb")) {
            holder.buttonWrite.setVisibility(View.GONE);
        }

        if (bleDescriptorDisplay.valueDisplay != null && bleDescriptorDisplay.valueDisplay.length > 0) {
            String dataDisplayHex = ByteUtils.getHexStringFromByteArray(bleDescriptorDisplay.valueDisplay,true);
            String dataDisplayAscii = new String(bleDescriptorDisplay.valueDisplay, StandardCharsets.US_ASCII);
            holder.textViewValueDisplayBytes.setText(dataDisplayHex);
            holder.textViewValueDisplayAscii.setText(dataDisplayAscii);
            holder.linearLayoutValueDisplay.setVisibility(View.VISIBLE);
        }else{
            holder.textViewValueDisplayBytes.setText(null);
            holder.textViewValueDisplayAscii.setText(null);
            holder.linearLayoutValueDisplay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return descriptorDisplayList.size();
    }

    public void update(String descriptorUUID, byte[] value){
        for(BleDescriptorDisplay descriptorDisplay : descriptorDisplayList){
            if(descriptorDisplay.uuid.equalsIgnoreCase(descriptorUUID)){
                descriptorDisplay.valueDisplay = value;
                notifyDataSetChanged();
                break;
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewDescriptorName;
        private final TextView textViewDescriptorUUID;
        private final TextView textViewValueDisplayBytes;
        private final TextView textViewValueDisplayAscii;
        private Button buttonRead, buttonWrite;
        private LinearLayout linearLayoutValueDisplay;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescriptorName = itemView.findViewById(R.id.textViewDescriptorName);
            textViewDescriptorUUID = itemView.findViewById(R.id.textViewDescriptorUUID);
            buttonRead = itemView.findViewById(R.id.buttonReadData);
            buttonWrite = itemView.findViewById(R.id.buttonWriteData);
            textViewValueDisplayBytes = itemView.findViewById(R.id.textViewValuesBytes);
            textViewValueDisplayAscii = itemView.findViewById(R.id.textViewValuesAscii);
            linearLayoutValueDisplay = itemView.findViewById(R.id.linearLayoutValueDisplay);

            buttonRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildItemClickListener != null) {
                        BleDescriptorDisplay bleDescriptorDisplay = descriptorDisplayList.get(getAdapterPosition());
                        onChildItemClickListener.onChildItemClicked(bleDescriptorDisplay, getAdapterPosition(), 1);  // Read
                    }
                }
            });

            buttonWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildItemClickListener != null) {
                        BleDescriptorDisplay descriptorDisplay = descriptorDisplayList.get(getAdapterPosition());
                        onChildItemClickListener.onChildItemClicked(descriptorDisplay, getAdapterPosition(), 2);  // Write
                    }
                }
            });
        }
    }

    public void setOnChildItemClickListener(OnChildItemClickListener onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }

    interface OnChildItemClickListener {
        public void onChildItemClicked(BleDescriptorDisplay descriptorDisplay, int itemPosition, int code);
    }
}