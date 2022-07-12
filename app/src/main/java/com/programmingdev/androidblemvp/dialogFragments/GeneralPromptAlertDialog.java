package com.programmingdev.androidblemvp.dialogFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.programmingdev.androidblemvp.R;

public class GeneralPromptAlertDialog extends DialogFragment {
    private DialogListener dialogListener;

    public static final String AlertTitleKey = "alertTitle";
    public static final String AlertDescriptionKey = "alertDescription";
    public static final String IconKey = "icon";
    public static final String PositiveButtonNameKey = "positiveButton";
    public static final String NegativeButtonNameKey = "negativeButton";

    public GeneralPromptAlertDialog() {
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
        View view = inflater.inflate(R.layout.fragment_alert_prompt_dialog, container, false);
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

        TextView textViewAlertTitle = view.findViewById(R.id.textViewAlertTitle);
        TextView textViewAlertDescription = view.findViewById(R.id.textViewAlertMessageDescription);
        ImageView imageViewIcon = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            String alertTitle = getArguments().getString(AlertTitleKey);
            String alertDescription = getArguments().getString(AlertDescriptionKey);
            int icon = getArguments().getInt(IconKey);

            String positiveButtonName = getArguments().getString(PositiveButtonNameKey);
            String negativeButtonName = getArguments().getString(NegativeButtonNameKey);

            if (alertTitle != null && !alertTitle.isEmpty()) {
                textViewAlertTitle.setText(alertTitle);
            }

            if (alertDescription != null && !alertDescription.isEmpty()) {
                textViewAlertDescription.setText(alertDescription);
            }
            if (icon >= 0) {
                imageViewIcon.setBackgroundResource(icon);
            }

            if (positiveButtonName != null && !positiveButtonName.isEmpty()) {
                buttonPositive.setText(positiveButtonName);
            }

            if (negativeButtonName != null && !negativeButtonName.isEmpty()) {
                buttonNegative.setText(negativeButtonName);
            }
        }

        buttonNegative.setOnClickListener(view1 -> {
            if (dialogListener != null) {
                dialogListener.onNegativeButtonClicked(null, false);
            }
            dismiss();
        });

        buttonPositive.setOnClickListener(v -> {
            if (dialogListener != null) {
                dialogListener.onPositiveButtonClicked(null, false);
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

    public interface DialogListener {
        void onPositiveButtonClicked(String inputText, boolean dialogCancelFlag);

        void onNegativeButtonClicked(String inputText, boolean dialogCancelFlag);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}