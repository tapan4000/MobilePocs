package com.example.tapanj.mapsdemo.interfaces.location;

import android.location.Location;

public interface IPeriodicLocationCallback{
    void onPeriodicLocationUpdateReceived(Location location);
    void onLocationCheckLogEventReceived(String message);
    void onFailedLocationUpdateStart(String failureReason);
}
