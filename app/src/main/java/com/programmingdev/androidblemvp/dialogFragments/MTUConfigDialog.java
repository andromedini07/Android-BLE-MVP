package com.programmingdev.androidblemvp.dialogFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.programmingdev.androidblemvp.R;
import com.programmingdev.androidblemvp.utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public class MTUConfigDialog extends DialogFragment {
    private DialogListener dialogListener;

    public MTUConfigDialog() {
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
        View view = inflater.inflate(R.layout.mtu_config_layout, container, false);
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

        EditText editTextMTUSizeEntry = view.findViewById(R.id.editTextDataEntry);

        buttonNegative.setOnClickListener(view1 -> {
            if (dialogListener != null) {
                dialogListener.onNegativeButtonClicked(null, false);
            }
            dismiss();
        });

        buttonPositive.setOnClickListener(v -> {
            String mtuSizeEntered = editTextMTUSizeEntry.getText().toString().trim();

            if (mtuSizeEntered.isEmpty()) {
                showSnackBar(view, "Please enter valid MTU Size", "OK", 2000);
                return;
            }

            int mtuSize = Integer.parseInt(mtuSizeEntered);
            if (mtuSize < 23) {
                showSnackBar(view, "The MTU size to request should not be lesser than 20 bytes", "OK", 5000);
                return;
            }

            if (dialogListener != null) {
                dialogListener.onPositiveButtonClicked(mtuSize, false);
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
        void onPositiveButtonClicked(int mtuSize, boolean dialogCancelFlag);

        void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}