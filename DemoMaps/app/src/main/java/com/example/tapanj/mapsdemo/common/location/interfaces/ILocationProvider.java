package com.example.tapanj.mapsdemo.common.location.interfaces;

import com.example.tapanj.mapsdemo.interfaces.ISuccessFailureCallback;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.location.PlaceInformation;
import com.google.android.gms.location.LocationCallback;

public interface ILocationProvider {
    void getCurrentLocation(WorkflowContext workflowContext, ILocationCallback locationCallback);
    LocationCallback startLocationUpdates(WorkflowContext workflowContext, final IPeriodicLocationCallback periodicLocationCallback);
    void stopLocationUpdates(WorkflowContext workflowContext, LocationCallback locationCallback);
    void getCurrentPlaceInformation(WorkflowContext workflowContext, ISuccessFailureCallback<PlaceInformation> placeInformationCallback);
}
