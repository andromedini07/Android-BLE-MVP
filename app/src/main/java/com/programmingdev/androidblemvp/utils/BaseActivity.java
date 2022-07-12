package com.programmingdev.androidblemvp.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.programmingdev.androidblemvp.Constants;
import com.programmingdev.androidblemvp.dialogFragments.GeneralAlertDialog;
import com.programmingdev.androidblemvp.dialogFragments.GeneralPromptAlertDialog;
import com.programmingdev.androidblemvp.location.LocationAccessPermissionCallback;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int requestAccessStorageDeviceCode = 12;
    private static final int requestCodeCoarseLocationSettings = 14;
    private static final int requestCodeAppSettings = 13;
    private static final int GPS_ENABLE_REQUEST = 0x1001;

    private static final String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

    public LocationAccessPermissionCallback locationAccessPermissionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void checkCoarseLocation() {
        PackageManager pm = this.getPackageManager();
        int hasPerm = pm.checkPermission(permissionAccessFineLocation, this.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            checkLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permissionAccessFineLocation},
                    requestCodeCoarseLocationSettings);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestCodeCoarseLocationSettings:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permission is granted, then proceed to Process Activity
                    checkLocation();
                } else {
                    // permission denied,Disable the functionality that depends on this permission.
                    // This flag indicates whether never ask again checkbox is ticked
                    boolean showRationale = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permissionAccessFineLocation);
                    }
                    if (showRationale) {
                        String title = "Location Permission Required";
                        String message = "This App does not use location information,but scanning for Bluetooth devices requires this permission." + "\n\n" +
                                "The scan result can contain location information,so Google decided that starting with " +
                                "Android 6.0(Marshmallow) apps have to request permission.";
                        showFirstAlertDialog(title, message, requestCode);
                    } else {
                        String title = "Permission Denied";
                        String message = "Without this permission the app is unable to scan Bluetooth Devices." + "\n\n" +
                                "Clicking OK will enable you to app settings. Then go to permission settings and enable Access Location";
                        showSecondAlertDialog(title, message, requestCode);
                    }
                }
                break;
        }
    }

    private void showFirstAlertDialog(String title, String message, final int permissionRequestCode) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (permissionRequestCode) {
                            case requestCodeCoarseLocationSettings:
                                checkCoarseLocation();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("I'm sure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showAlertDialog(0, Constants.alertTitle, "App cannot go further due to Denial of Permission",
                                new GeneralAlertDialog.DialogListener() {
                                    @Override
                                    public void onFinishEditDialog(String inputText, boolean dialogCancelFlag) {
                                        finish();
                                    }
                                });
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    private void showSecondAlertDialog(String title, String message, final int permissionRequestCode) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, permissionRequestCode);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeCoarseLocationSettings) {
            PackageManager pm = this.getPackageManager();
            int hasPerm = pm.checkPermission(permissionAccessFineLocation, this.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                checkLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Location Permission is not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocation() {
        console.log(TAG, "Check");
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            if (locationAccessPermissionCallback != null) {
                locationAccessPermissionCallback.onLocationDisabled();
            }
//            showAlertDialog(0, "Location Services", "Location services not enabled",
//                    (inputText, dialogCancelFlag) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        } else {
            if (locationAccessPermissionCallback != null) {
                locationAccessPermissionCallback.onLocationEnabled();
                locationAccessPermissionCallback.onLocationAccessPermissionGranted();
            }
        }
    }

    public void showAlertPromptDialog(int icon, String alertTitle, String alertMessage, String positiveButtonName, String negativeButtonName, GeneralPromptAlertDialog.DialogListener callback) {
        GeneralPromptAlertDialog dialogFragment = new GeneralPromptAlertDialog();

        Bundle bundle = new Bundle();
        bundle.putString(GeneralPromptAlertDialog.AlertTitleKey, alertTitle);
        bundle.putString(GeneralPromptAlertDialog.AlertDescriptionKey, alertMessage);
        bundle.putInt(GeneralPromptAlertDialog.IconKey, icon);
        bundle.putString(GeneralPromptAlertDialog.PositiveButtonNameKey, positiveButtonName);
        bundle.putString(GeneralPromptAlertDialog.NegativeButtonNameKey, negativeButtonName);

        dialogFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, "dialog");
        dialogFragment.setDialogListener(callback);
    }

    public void showAlertDialog(int icon, String alertTitle, String alertMessage, GeneralAlertDialog.DialogListener callback) {
        GeneralAlertDialog dialogFragment = new GeneralAlertDialog();
        Bundle bundle = new Bundle();
        bundle.putString(GeneralAlertDialog.AlertTitle, alertTitle);
        bundle.putString(GeneralPromptAlertDialog.AlertDescriptionKey, alertMessage);
        bundle.putInt(GeneralPromptAlertDialog.IconKey, icon);
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, "dialog");
        dialogFragment.setDialogListener(callback);
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //Do your stuff on GPS status change
                console.log(TAG, "Location mode changed");
//                Toast.makeText(getApplicationContext(),"Change",Toast.LENGTH_SHORT).show();
                checkLocation();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gpsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        console.log(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        console.log(TAG, "onPause");
    }
}