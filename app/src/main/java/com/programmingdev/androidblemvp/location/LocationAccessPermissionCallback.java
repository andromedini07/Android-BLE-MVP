package com.programmingdev.androidblemvp.location;

public interface LocationAccessPermissionCallback {
    public void onLocationAccessPermissionGranted();
    public void onLocationAccessPermissionNotGranted();
    public void onLocationDisabled();
    public void onLocationEnabled();
}
