package com.example.tapanj.mapsdemo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class LocationActivityBase extends AppCompatActivity {
    private final int REQUEST_GPS_SETTINGS = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 2;

    protected final int DEFAULT_ZOOM = 15;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made.
                        onCompleteLocationCheck(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings but chose not to.
                        onCompleteLocationCheck(false);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    protected void checkLocationPermission(WorkflowContext workflowContext) {
        // Check if the location permissions are available
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onLocationCheckLogEventReceived("Location permission not available");

            // Permission is not granted. Ask the user to grant the permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                onLocationCheckLogEventReceived("Should show permission rationale. Requesting permission");
                // Show an explanation to the user asynchronously on why the permission is needed. Do not block this thread waiting for the user's response.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                onLocationCheckLogEventReceived("Should not show permission rationale. Requesting permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            onLocationCheckLogEventReceived("Location permission available.");
            // If the location permissions are available, then check if GPS is available.
            askForGps();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
                case REQUEST_ACCESS_FINE_LOCATION:
                // If the request is cancelled, the results array is empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Perform the location related task.
                    askForGps();
                } else {
                    // Permission has been denied.
                }
                break;
        }
    }

    protected abstract void onCompleteLocationCheck(boolean isConnectSuccessful);

    protected abstract void onLocationCheckLogEventReceived(String logEvent);

    private void askForGps(){
        LocationRequest highAccuracyLocationRequest = new LocationRequest();
        highAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        highAccuracyLocationRequest.setInterval(10000);
        highAccuracyLocationRequest.setFastestInterval(5000);

//        LocationRequest balancedPowerAccuracyLocationRequest = new LocationRequest();
//        balancedPowerAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        balancedPowerAccuracyLocationRequest.setInterval(10000);
//        balancedPowerAccuracyLocationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(highAccuracyLocationRequest);
//                .addLocationRequest(balancedPowerAccuracyLocationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            onLocationCheckLogEventReceived("Location service response received.");
                            LocationSettingsResponse response = task.getResult(ApiException.class);

                            // If the GPS is available, fetch the current location.
                            onCompleteLocationCheck(true);
                        } catch (ApiException ex) {
                            onLocationCheckLogEventReceived("Exception received: " + ex.getMessage());
                            switch (ex.getStatusCode()){
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied, but could be fixed by showing the user a dialog
                                    try{
                                        onLocationCheckLogEventReceived("Resolution required Exception received. Sending request GPS enable request.");

                                        // Cast the resolvable exception
                                        ResolvableApiException resolvable = (ResolvableApiException) ex;

                                        // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult.
                                        resolvable.startResolutionForResult(LocationActivityBase.this, REQUEST_GPS_SETTINGS);
                                    }
                                    catch(IntentSender.SendIntentException exception){
                                        onLocationCheckLogEventReceived("Send Intent exception received." + ex.getMessage());
                                    }
                                    catch (ClassCastException exception){
                                        onLocationCheckLogEventReceived("Class cast exception received." + ex.getMessage());
                                    }

                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog
                                    onLocationCheckLogEventReceived("GPS Settings change is unavailable. No way to fix settings");
                                    break;
                            }
                        }
                    }
                });
    }
}
