package com.example.tapanj.mapsdemo.common.location.interfaces;

import android.location.Location;
import com.google.android.gms.common.api.ResolvableApiException;

public interface ILocationCallback{
    void onLocationCheckLogEventReceived(String message);

    void onCurrentLocationRequestComplete(Location location);

    void onLocationRequestFailure(String failureReason);

    void onRequestEnableGpsSettingRequired(ResolvableApiException ex);
}
