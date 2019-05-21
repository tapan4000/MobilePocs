package com.example.tapanj.mapsdemo.common.location.interfaces;

import android.location.Location;

public interface IPeriodicLocationCallback{
    void onPeriodicLocationUpdateReceived(Location location);
    void onLocationCheckLogEventReceived(String message);
    void onFailedLocationUpdateStart(String failureReason);
}
