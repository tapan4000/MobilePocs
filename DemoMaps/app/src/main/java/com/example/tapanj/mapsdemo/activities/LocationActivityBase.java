package com.example.tapanj.mapsdemo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class LocationActivityBase extends AppCompatActivity {
    private final int REQUEST_GPS_SETTINGS = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 2;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mfusedLocationProviderClient;

    protected final int DEFAULT_ZOOM = 15;
    protected WorkflowContext activityLifecycleWorkflowContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLocationUpdateCallback();
    }

    protected abstract void initializeActivityLifecycleWorkflowContext();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made.
                        onLocationPermissionCheckComplete(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings but chose not to.
                        onLocationPermissionCheckComplete(false);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    protected void getCurrentLocation(WorkflowContext workflowContext) {
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

    protected void onLocationPermissionCheckComplete(boolean isConnectSuccessful){
        if(isConnectSuccessful){
            fetchCurrentLocationPostPermissionChecks();
        }
        else{

        }
    }

    private boolean checkAccessFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        return true;
    }

    protected abstract void onLocationUpdateReceived(Location location);

    private void initializeLocationUpdateCallback() {
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationCheckLogEventReceived("Location callback invoked.");
                //logger.LogVerbose("Location callback invoked.", callbackWorkflowId);
                if (null == locationResult) {
                    onLocationCheckLogEventReceived("Location result is null.");
                    onLocationUpdateReceived(null);
                    //logger.LogError("Location result is null", callbackWorkflowId);
                    return;
                }

                for(Location location: locationResult.getLocations()){
                    // Store the location information in local file.
                    onLocationUpdateReceived(location);
                }
            }
        };
    }

    protected void startLocationUpdates(WorkflowContext workflowContext) {
        if(null != mfusedLocationProviderClient){
            if (!checkAccessFineLocationPermission())
                return;

            LocationRequest highAccuracyLocationRequest = new LocationRequest();
            highAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            highAccuracyLocationRequest.setInterval(10000);
            highAccuracyLocationRequest.setFastestInterval(5000);
            mfusedLocationProviderClient.requestLocationUpdates(highAccuracyLocationRequest, mLocationCallback, null);
        }
    }

    protected void stopLocationUpdates(WorkflowContext workflowContext) {
        if(null != mfusedLocationProviderClient){
            mfusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void fetchCurrentLocationPostPermissionChecks() {
        onLocationCheckLogEventReceived("Entered fetch location post validations.");
        if (!checkAccessFineLocationPermission())
        {
            onLocationCheckLogEventReceived("Access fine location permissions are not available.");
            onCurrentLocationRequestComplete(null);
            return;
        }

        if(null == mfusedLocationProviderClient){
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (null != location) {
                    onCurrentLocationRequestComplete(location);
                } else {
                    // The location may be null 1) if the location has been turned off in the device. 2) If the location was previously retreived because
                    // disabling the location clears the cache. 3) The device never recorded the location in case of a new device. 4) Google play service
                    // on the device has restarted and there is no active fused location provider client. To avoid these situations you can create a new
                    // client and request location updates yourself.
                    onLocationCheckLogEventReceived("Location object is null");
                    onCurrentLocationRequestComplete(null);
                }
            }
        });
    }

    protected String getLocationString(Location location) {
        String provider = location.getProvider();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long time = location.getTime();

        float accuracy = -1;
        if(location.hasAccuracy()){
            accuracy = location.getAccuracy();
        }

        double altitude = -1;
        if(location.hasAltitude()){
            altitude = location.getAltitude();
        }

        float speed = -1;
        if(location.hasSpeed()){
            speed = location.getSpeed();
        }

        float bearing = -1;
        if(location.hasBearing())
        {
            bearing = location.getBearing();
        }

        String strDate = getCurrentDateTime();

        String locationDetail = strDate + " - Provider:" + provider + ", Latitude:" + latitude + ", Longitude:" + longitude + ", Time:" + time + ", Accuracy:" + accuracy
                + ", Altitude:" + altitude + ", Speed:" + speed + ", Bearing:" + bearing;

        return locationDetail;
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    protected abstract void onLocationCheckLogEventReceived(String logEvent);

    protected abstract void onCurrentLocationRequestComplete(Location location);

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
                            onLocationPermissionCheckComplete(true);
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
