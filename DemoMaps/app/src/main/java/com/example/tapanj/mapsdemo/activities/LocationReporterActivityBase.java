package com.example.tapanj.mapsdemo.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.example.tapanj.mapsdemo.viewmodel.LocationViewModel;
import com.example.tapanj.mapsdemo.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public abstract class LocationReporterActivityBase extends ActivityBase {
    protected LocationViewModel locationViewModel;

    private final int REQUEST_ACCESS_FINE_LOCATION = 2;

    @Inject
    ViewModelFactory viewModelFactory;

    protected LocationReporterActivityBase(){
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                // If the request is cancelled, the results array is empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locationViewModel = ViewModelProviders.of(this, this.viewModelFactory).get(LocationViewModel.class);
    }

    private boolean checkAccessFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Show an explanation to the user asynchronously on why the permission is needed. Do not block this thread waiting for the user's response.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }

            return false;
        }
        return true;
    }
}
