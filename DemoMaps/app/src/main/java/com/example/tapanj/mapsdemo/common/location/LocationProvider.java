package com.example.tapanj.mapsdemo.common.location;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.example.tapanj.mapsdemo.interfaces.ISuccessFailureCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.common.location.interfaces.IPeriodicLocationCallback;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.location.PlaceInformation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class LocationProvider implements ILocationProvider {
    private Context applicationContext;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private PlaceDetectionClient placeDetectionClient;

    @Inject
    public LocationProvider(ContextWrapper contextWrapper){
        this.applicationContext = contextWrapper;
    }

    public void getCurrentLocation(WorkflowContext workflowContext, ILocationCallback locationCallback) {
        // Check if the location permissions are available
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationCallback.onLocationCheckLogEventReceived("Location permission not available");

            // Permission is not granted. Ask the user to grant the permission.
            locationCallback.onLocationRequestFailure("Location permission not available");
        } else {
            locationCallback.onLocationCheckLogEventReceived("Location permission available.");
            // If the location permissions are available, then check if GPS is available.
            askForGps(locationCallback);
        }
    }

    public void getCurrentPlaceInformation(WorkflowContext workflowContext, final ISuccessFailureCallback<PlaceInformation> placeInformationCallback){
        if(null == this.placeDetectionClient){
            this.placeDetectionClient = Places.getPlaceDetectionClient(this.applicationContext);
            if (!this.checkAccessFineLocationPermission()) {
                return;
            }

            Task<PlaceLikelihoodBufferResponse> placeResult = this.placeDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    if (task.isSuccessful() && null != task.getResult()) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            PlaceInformation placeInformation = new PlaceInformation((String) placeLikelihood.getPlace().getName(),
                                    (String) placeLikelihood.getPlace().getAddress(),
                                    (String) placeLikelihood.getPlace().getAttributions(),
                                    placeLikelihood.getPlace().getLatLng());
                            placeInformationCallback.onSuccess(placeInformation);
                            break;
                        }
                        // Release the place likelyhood buffer to avoid memory leaks.
                        likelyPlaces.release();
                    } else {
                        Exception ex = task.getException();
                        placeInformationCallback.onFailure(ex.getMessage());
                    }
                }
            });
        }
    }

    private void onLocationPermissionCheckComplete(boolean isConnectSuccessful, ILocationCallback locationCallback){
        if(isConnectSuccessful){
            fetchCurrentLocationPostPermissionChecks(locationCallback);
        }
        else{

        }
    }

    private boolean checkAccessFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public LocationCallback startLocationUpdates(WorkflowContext workflowContext, final IPeriodicLocationCallback periodicLocationCallback) {
        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                periodicLocationCallback.onLocationCheckLogEventReceived("Location callback invoked.");
                //logger.LogVerbose("Location callback invoked.", callbackWorkflowId);
                if (null == locationResult) {
                    periodicLocationCallback.onLocationCheckLogEventReceived("Location result is null.");
                    periodicLocationCallback.onPeriodicLocationUpdateReceived(null);
                    return;
                }

                for(Location location: locationResult.getLocations()){
                    // Store the location information in local file.
                    periodicLocationCallback.onPeriodicLocationUpdateReceived(location);
                }
            }
        };

        if(null == this.mfusedLocationProviderClient){
            this.mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.applicationContext);
        }

        if (!checkAccessFineLocationPermission()){
            periodicLocationCallback.onLocationCheckLogEventReceived("Fine location permissions not available.");
            periodicLocationCallback.onFailedLocationUpdateStart("Fine location permissions not available.");
            return null;
        }


        LocationRequest highAccuracyLocationRequest = new LocationRequest();
        highAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        highAccuracyLocationRequest.setInterval(45000);
        highAccuracyLocationRequest.setFastestInterval(30000);
        this.mfusedLocationProviderClient.requestLocationUpdates(highAccuracyLocationRequest, locationCallback, null);
        return locationCallback;
    }

    public void stopLocationUpdates(WorkflowContext workflowContext, LocationCallback locationCallback) {
        if(null != mfusedLocationProviderClient){
            mfusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private void fetchCurrentLocationPostPermissionChecks(final ILocationCallback locationCallback) {
        locationCallback.onLocationCheckLogEventReceived("Entered fetch location post validations.");
        if (!checkAccessFineLocationPermission())
        {
            locationCallback.onLocationCheckLogEventReceived("Access fine location permissions are not available.");
            locationCallback.onLocationRequestFailure("Access fine location permissions are not available.");
            return;
        }

        if(null == mfusedLocationProviderClient){
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.applicationContext);
        }

        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (null != location) {
                    locationCallback.onCurrentLocationRequestComplete(location);
                } else {
                    // The location may be null 1) if the location has been turned off in the device. 2) If the location was previously retreived because
                    // disabling the location clears the cache. 3) The device never recorded the location in case of a new device. 4) Google play service
                    // on the device has restarted and there is no active fused location provider client. To avoid these situations you can create a new
                    // client and request location updates yourself.
                    locationCallback.onLocationCheckLogEventReceived("Location object is null");
                    locationCallback.onCurrentLocationRequestComplete(null);
                }
            }
        });
    }

    private void askForGps(final ILocationCallback locationCallback){
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

        LocationServices.getSettingsClient(this.applicationContext)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            locationCallback.onLocationCheckLogEventReceived("Location service response received.");
                            LocationSettingsResponse response = task.getResult(ApiException.class);

                            // If the GPS is available, fetch the current location.
                            onLocationPermissionCheckComplete(true, locationCallback);
                        } catch (ApiException ex) {
                            locationCallback.onLocationCheckLogEventReceived("Exception received: " + ex.getMessage());
                            switch (ex.getStatusCode()){
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied, but could be fixed by showing the user a dialog
                                    try{
                                        locationCallback.onLocationCheckLogEventReceived("Resolution required Exception received. Sending request GPS enable request.");

                                        locationCallback.onLocationRequestFailure("Resolution required Exception received. Sending request GPS enable request.");

                                        // Cast the resolvable exception
                                        ResolvableApiException resolvable = (ResolvableApiException) ex;

                                        locationCallback.onRequestEnableGpsSettingRequired(resolvable);
                                    }
                                    catch (ClassCastException exception){
                                        locationCallback.onLocationCheckLogEventReceived("Class cast exception received." + ex.getMessage());
                                        locationCallback.onLocationRequestFailure("Class cast exception received." + ex.getMessage());
                                    }

                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog
                                    locationCallback.onLocationCheckLogEventReceived("GPS Settings change is unavailable. No way to fix settings");

                                    locationCallback.onLocationRequestFailure("GPS Settings change is unavailable. No way to fix settings");
                                    break;
                            }
                        }
                    }
                });
    }
}
