package com.example.tapanj.mapsdemo.intentservice;

import android.content.Intent;
import com.example.tapanj.mapsdemo.models.IntentServiceResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceTransitionIntentService extends IntentServiceBase {

    public GeofenceTransitionIntentService() {
        super(GeofenceTransitionIntentService.class.getName());
    }

    @Override
    protected IntentServiceResult ProcessIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage = "Error occured while reading geofence intent. Error Code:" + geofencingEvent.getErrorCode();
            this.logger.LogError(errorMessage, this.workflowContext.getWorkflowId());
        }

        // Get the transition type
        int geofenceTransitionType = geofencingEvent.getGeofenceTransition();

        if(geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
            // Get the geofences that were trigerred. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String
            String geofenceTransitionDetails = getGeofenceTransitionDetails(this,
                    geofenceTransitionType,
                    triggeringGeofences);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            this.logger.LogInformation(geofenceTransitionDetails, this.workflowContext.getWorkflowId());
        }
        else{
            String message = "Invalid transition type received. Received: " + geofenceTransitionType;
            this.logger.LogInformation(message, this.workflowContext.getWorkflowId());
        }
        return null;
    }

    private void sendNotification(String geofenceTransitionDetails) {
    }

    private String getGeofenceTransitionDetails(GeofenceTransitionIntentService geofenceTransitionIntentService, int geofenceTransitionType, List<Geofence> triggeringGeofences) {
        String concatenatedResult = "";
        for (Geofence triggeringGeofence:
             triggeringGeofences) {
            concatenatedResult += triggeringGeofence.toString();
        }

        return concatenatedResult;
    }
}
