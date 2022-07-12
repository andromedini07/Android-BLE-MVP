package com.programmingdev.androidblemvp.dialogFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.utils.ByteUtils;
import com.programmingdev.androidblemvp.utils.HexInputFilter;

import java.nio.charset.StandardCharsets;

public class DataConfigDialog extends DialogFragment {
    private DialogListener dialogListener;

    public DataConfigDialog() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_config_layout, container, false);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonNegative = view.findViewById(R.id.buttonNegative);
        Button buttonPositive = view.findViewById(R.id.buttonPositive);

        TextView textViewHexRepresentation = view.findViewById(R.id.textViewHexRepresentation);

        EditText editTextDataEntry = view.findViewById(R.id.editTextDataEntry);
        Spinner spinnerDataType = view.findViewById(R.id.spinnerDataType);

        String[] dataTypeArray = new String[]{"Bytes", "Text"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, dataTypeArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataType.setAdapter(arrayAdapter);

        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDataType = arrayAdapter.getItem(position);
                editTextDataEntry.setText("");
                switch (selectedDataType) {
                    case "Bytes":
                        textViewHexRepresentation.setVisibility(View.VISIBLE);
                        editTextDataEntry.setFilters(new InputFilter[]{new HexInputFilter()});
                        editTextDataEntry.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        break;
                    case "Text":
                        textViewHexRepresentation.setVisibility(View.GONE);
                        editTextDataEntry.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonNegative.setOnClickListener(view1 -> {
            if (dialogListener != null) {
                dialogListener.onNegativeButtonClicked(null, false);
            }
            dismiss();
        });

        buttonPositive.setOnClickListener(v -> {
            String selectedDataType = (String) spinnerDataType.getSelectedItem();
            String inputDataEntered = editTextDataEntry.getText().toString().trim();

            if (inputDataEntered.isEmpty()) {
                showSnackBar(view, "Please enter valid data", "OK", 2000);
                return;
            }

            byte[] dataBytes = null;
            switch (selectedDataType) {
                case "Bytes":
                    // Check if the data entered is a valid Byte Array
                    // Hex Data => 1 byte = 2 characters
                    if (inputDataEntered.length() % 2 == 0) {
                        dataBytes = ByteUtils.getByteArrayFromHexString(inputDataEntered);
                    } else {
                        showSnackBar(view, "Please enter a valid Byte Array", "OK", 2000);
                        return;
                    }
                    break;
                case "Text":
                default:
                    dataBytes = inputDataEntered.getBytes(StandardCharsets.UTF_8);
                    break;
            }

            if (dialogListener != null) {
                dialogListener.onPositiveButtonClicked(dataBytes, false);
            }
            dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom);
        setCancelable(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showSnackBar(View view, String message, String actionMessage, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction(actionMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorInActiveMenu));
        snackbar.show();
    }

    public interface DialogListener {
        void onPositiveButtonClicked(byte[] inputData, boolean dialogCancelFlag);
        void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}