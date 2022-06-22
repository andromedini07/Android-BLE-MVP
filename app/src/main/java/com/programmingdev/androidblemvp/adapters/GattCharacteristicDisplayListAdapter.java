package com.programmingdev.androidblemvp.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.models.BleCharacteristicsDisplay;
import com.programmingdev.androidblemvp.models.BleDescriptorDisplay;
import com.programmingdev.androidblemvp.utils.ByteUtils;
import com.programmingdev.blecommmodule.Property;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GattCharacteristicDisplayListAdapter extends RecyclerView.Adapter<GattCharacteristicDisplayListAdapter.ItemViewHolder> {

    private List<BleCharacteristicsDisplay> bleCharacteristicsDisplayList;
    private List<GattDescriptorDisplayListAdapter> childAdapters;
    private OnItemClickListener onItemClickListener;

    public GattCharacteristicDisplayListAdapter(List<BleCharacteristicsDisplay> characteristicsDisplayList) {
        this.bleCharacteristicsDisplayList = characteristicsDisplayList;
        if (this.bleCharacteristicsDisplayList == null) {
            this.bleCharacteristicsDisplayList = new ArrayList<>();
        }
        childAdapters = new ArrayList<>();
        for (BleCharacteristicsDisplay bleCharacteristicsDisplay : this.bleCharacteristicsDisplayList) {
            List<BleDescriptorDisplay> descriptorDisplayList = bleCharacteristicsDisplay.descriptorDisplayList;
            GattDescriptorDisplayListAdapter adapter = new GattDescriptorDisplayListAdapter(descriptorDisplayList);
            childAdapters.add(adapter);
        }
    }

    public GattCharacteristicDisplayListAdapter() {
        this.bleCharacteristicsDisplayList = new ArrayList<>();
        childAdapters = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gatt_characteristic, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BleCharacteristicsDisplay characteristicsDisplay = bleCharacteristicsDisplayList.get(position);
        String characteristicName = "Unknown Characteristic";
        if (characteristicsDisplay.name != null && !characteristicsDisplay.name.isEmpty()) {
            characteristicName = characteristicsDisplay.name;
        }
        holder.textViewCharacteristicName.setText(characteristicName);
        holder.textViewCharacteristicUUID.setText(characteristicsDisplay.uuid);

        List<Property> propertyList = characteristicsDisplay.properties;
        StringBuilder stringBuilder = new StringBuilder();
        for (Property property : propertyList) {
            if (property.equals(Property.READ)) {
                holder.buttonRead.setVisibility(View.VISIBLE);
                if (propertyList.size() == 1) {
                    stringBuilder.append("READ");
                } else if (propertyList.size() > 1) {
                    stringBuilder.append(", READ");
                }
            }
            if (property.equals(Property.WRITE)) {
                holder.buttonWrite.setVisibility(View.VISIBLE);
                if (propertyList.size() == 1) {
                    stringBuilder.append("WRITE");
                } else {
                    stringBuilder.append(", WRITE");
                }
            } else if (property.equals(Property.WRITE_NO_RESPONSE)) {
                holder.buttonWrite.setVisibility(View.VISIBLE);
                if (propertyList.size() == 1) {
                    stringBuilder.append("WRITE NO RESPONSE, ");
                } else {
                    stringBuilder.append(", WRITE NO RESPONSE");
                }
            }
            if (property.equals(Property.NOTIFY)) {
                List<BleDescriptorDisplay> descriptorDisplayList = characteristicsDisplay.descriptorDisplayList;
                if (descriptorDisplayList != null && !descriptorDisplayList.isEmpty()) {
                    holder.buttonNotify.setVisibility(View.VISIBLE);
                }
                if (propertyList.size() == 1) {
                    stringBuilder.append("NOTIFY");
                } else {
                    stringBuilder.append(", NOTIFY");
                }
            } else if (property.equals(Property.INDICATE)) {
                List<BleDescriptorDisplay> descriptorWrapperList = characteristicsDisplay.descriptorDisplayList;
                if (descriptorWrapperList != null && !descriptorWrapperList.isEmpty()) {
                    holder.buttonIndicate.setVisibility(View.VISIBLE);
                }
                if (propertyList.size() > 1) {
                    stringBuilder.append("INDICATE");
                } else {
                    stringBuilder.append(", INDICATE");
                }
            }
        }

        holder.textViewCharacteristicProperties.setText(stringBuilder.toString());

        if (characteristicsDisplay.valueDisplay != null && characteristicsDisplay.valueDisplay.length > 0) {
            holder.linearLayoutValueDisplay.setVisibility(View.VISIBLE);
            String dataHexDisplay = ByteUtils.getHexStringFromByteArray(characteristicsDisplay.valueDisplay, true);
            String asciiDisplay = new String(characteristicsDisplay.valueDisplay, StandardCharsets.US_ASCII);
            holder.textViewValueBytes.setText(dataHexDisplay);
            holder.textViewValueAscii.setText(asciiDisplay);
        } else {
            holder.textViewValueBytes.setText(null);
            holder.textViewValueAscii.setText(null);
            holder.linearLayoutValueDisplay.setVisibility(View.GONE);
        }

        if (characteristicsDisplay.isNotificationEnabled) {
            if (holder.buttonIndicate.getVisibility() == View.VISIBLE) {
                holder.buttonIndicate.setText("Disable Indication");
            } else if (holder.buttonNotify.getVisibility() == View.VISIBLE) {
                holder.buttonIndicate.setText("Disable Notification");
            }
        } else {
            if (holder.buttonIndicate.getVisibility() == View.VISIBLE) {
                holder.buttonIndicate.setText("Indicate");
            } else if (holder.buttonNotify.getVisibility() == View.VISIBLE) {
                holder.buttonIndicate.setText("Notify");
            }
        }

        // Child Items
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        if (!childAdapters.isEmpty()) {
            GattDescriptorDisplayListAdapter adapterChild = childAdapters.get(position);
            if (adapterChild != null) {
                holder.recyclerView.setAdapter(adapterChild);
                adapterChild.setOnChildItemClickListener(new GattDescriptorDisplayListAdapter.OnChildItemClickListener() {
                    @Override
                    public void onChildItemClicked(BleDescriptorDisplay bleDescriptorDisplay, int childItemPosition, int code) {
                        if (onItemClickListener != null) {
                            BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(position);
                            onItemClickListener.onChildItemClicked(bleDescriptorDisplay, childItemPosition, bleCharacteristicsDisplay, position, code);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return bleCharacteristicsDisplayList.size();
    }

    public void update(List<BleCharacteristicsDisplay> bleCharacteristicsDisplays) {
        this.bleCharacteristicsDisplayList = bleCharacteristicsDisplays;
        for (BleCharacteristicsDisplay bleCharacteristicsDisplay : this.bleCharacteristicsDisplayList) {
            List<BleDescriptorDisplay> descriptorDisplayList = bleCharacteristicsDisplay.descriptorDisplayList;
            GattDescriptorDisplayListAdapter adapter = new GattDescriptorDisplayListAdapter(descriptorDisplayList);
            childAdapters.add(adapter);
        }
        notifyDataSetChanged();
    }

    public void update(String characteristicUUID, byte[] data) {
        for (BleCharacteristicsDisplay characteristicsDisplay : bleCharacteristicsDisplayList) {
            if (characteristicsDisplay.uuid.equalsIgnoreCase(characteristicUUID)) {
                characteristicsDisplay.valueDisplay = data;
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void update(String characteristicUUID, String descriptorUUID, byte[] data) {
        for (int i = 0; i < bleCharacteristicsDisplayList.size(); i++) {
            BleCharacteristicsDisplay characteristicsDisplay = bleCharacteristicsDisplayList.get(i);
            if (characteristicsDisplay.uuid.equalsIgnoreCase(characteristicUUID)) {
                GattDescriptorDisplayListAdapter adapter = childAdapters.get(i);
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.update(descriptorUUID, data);
                    notifyDataSetChanged();
                });
                break;
            }
        }
    }

    public void update(String characteristicUUID, boolean enabled) {
        for (BleCharacteristicsDisplay characteristicsDisplay : bleCharacteristicsDisplayList) {
            if (characteristicsDisplay.uuid.equalsIgnoreCase(characteristicUUID)) {
                characteristicsDisplay.isNotificationEnabled = enabled;
                notifyDataSetChanged();
                break;
            }
        }
    }

    public List<BleCharacteristicsDisplay> getList(){
        return this.bleCharacteristicsDisplayList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCharacteristicName;
        private final TextView textViewCharacteristicUUID;
        private final TextView textViewCharacteristicProperties;
        private final Button buttonRead;
        private final Button buttonWrite;
        private final Button buttonWriteNoResponse;
        private final Button buttonNotify;
        private final Button buttonIndicate;
        private final LinearLayout layoutExpandView;
        private final RecyclerView recyclerView;
        private final LinearLayout linearLayoutValueDisplay;
        private final TextView textViewValueBytes;
        private final TextView textViewValueAscii;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCharacteristicName = itemView.findViewById(R.id.textViewCharacteristicName);
            textViewCharacteristicUUID = itemView.findViewById(R.id.textViewCharacteristicUUID);
            textViewCharacteristicProperties = itemView.findViewById(R.id.textViewProperties);
            buttonRead = itemView.findViewById(R.id.buttonReadData);
            buttonWrite = itemView.findViewById(R.id.buttonWriteData);
            buttonWriteNoResponse = itemView.findViewById(R.id.buttonWriteNoResponse);
            buttonNotify = itemView.findViewById(R.id.buttonEnableNotification);
            buttonIndicate = itemView.findViewById(R.id.buttonEnableIndication);
            layoutExpandView = itemView.findViewById(R.id.linearLayoutDescriptorListDisplay);
            recyclerView = itemView.findViewById(R.id.recyclerViewDescriptor);
            linearLayoutValueDisplay = itemView.findViewById(R.id.linearLayoutValueDisplay);
            textViewValueBytes = itemView.findViewById(R.id.textViewValuesBytes);
            textViewValueAscii = itemView.findViewById(R.id.textViewValuesAscii);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                    List<BleDescriptorDisplay> bleDescriptorDisplays = bleCharacteristicsDisplay.descriptorDisplayList;
                    if (bleDescriptorDisplays != null && !bleDescriptorDisplays.isEmpty()) {
                        if (!layoutExpandView.isShown()) {
//                            layoutExpandView.measure(
//                                    View.MeasureSpec.makeMeasureSpec(((View) layoutExpandView.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
//                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//                            );

//                            layoutExpandView.measure(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

//                            int height = layoutExpandView.getMeasuredHeight();
//                            expand(layoutExpandView, 200, height);
                            expand(layoutExpandView);
                        } else {
                            collapse(layoutExpandView);
//                            collapse(layoutExpandView, 200, 0);
                        }
                    }
                }
            });

            buttonRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                        onItemClickListener.onParentItemClicked(bleCharacteristicsDisplay, getAdapterPosition(), 1); // Read
                    }
                }
            });

            buttonWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                        onItemClickListener.onParentItemClicked(bleCharacteristicsDisplay, getAdapterPosition(), 2); // Write
                    }
                }
            });

            buttonWriteNoResponse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                        onItemClickListener.onParentItemClicked(bleCharacteristicsDisplay, getAdapterPosition(), 3); // Write
                    }
                }
            });

            buttonNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                        onItemClickListener.onParentItemClicked(bleCharacteristicsDisplay, getAdapterPosition(), 4); // Notify
                    }
                }
            });

            buttonIndicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        BleCharacteristicsDisplay bleCharacteristicsDisplay = bleCharacteristicsDisplayList.get(getAdapterPosition());
                        onItemClickListener.onParentItemClicked(bleCharacteristicsDisplay, getAdapterPosition(), 5); // Indicate
                    }
                }
            });
        }
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onParentItemClicked(BleCharacteristicsDisplay bleCharacteristicsDisplay, int parentItemPosition, int code);

        public void onChildItemClicked(BleDescriptorDisplay bleDescriptorDisplay, int childItemPosition, BleCharacteristicsDisplay bleCharacteristicsDisplay, int parentItemPosition, int code);
    }
}