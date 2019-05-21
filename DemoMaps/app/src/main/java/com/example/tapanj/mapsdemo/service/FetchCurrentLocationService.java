package com.example.tapanj.mapsdemo.service;

import android.app.*;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.Utility.LocationHelper;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogger;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.common.location.interfaces.IPeriodicLocationCallback;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import dagger.android.AndroidInjection;

import javax.inject.Inject;
import java.util.Calendar;

public class FetchCurrentLocationService extends Service {
    private WorkflowContext workflowContext;
    private final FetchCurrentLocationServiceBinder fetchCurrentLocationServiceBinder = new FetchCurrentLocationServiceBinder();
    private String currentStatus;
    private String lastButOneStatus;
    private String lastButTwoStatus;
    private String lastButThreeStatus;
    private String lastButFourStatus;
    private String lastButFiveStatus;

    @Inject
    ILocationProvider locationProvider;

    @Inject
    ILogger logger;
    private int counter = 0;
    Handler handler = new Handler();
    private Runnable periodicLocationFetch = new Runnable() {
        @Override
        public void run() {
//            stopSelf();
            postCurrentStatus("Runnable called" + counter);
            if(counter > 1000){
                logger.LogInformation("Stopping service", workflowContext.getWorkflowId());
                postCurrentStatus("Stopping service");
                stopForeground(true);
                stopSelf();
            }
            else{
                counter++;
                handler.postDelayed(periodicLocationFetch, 30*1000);
                fetchLocation();
            }
        }
    };

    private LocationCallback periodicLocationCallback;

    public class FetchCurrentLocationServiceBinder extends Binder{
        public FetchCurrentLocationService getService(){
            return FetchCurrentLocationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null == intent){
            this.postCurrentStatus("Start command called with null intent");
            logger.LogInformation("Start command called with null intent", workflowContext.getWorkflowId());
        }
        else{
            this.postCurrentStatus("Start Command called");
        }

        logger.LogInformation("Service - removing callbacks from handler, so only one flow runs.", this.workflowContext.getWorkflowId());

        handler.removeCallbacks(periodicLocationFetch);
        logger.LogInformation("Service on start command", this.workflowContext.getWorkflowId());

        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, GroupListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID
//                    ,Constants.NOTIFICATION_CHANNEL_NAME
//                    ,NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentTitle("Location being captured")
                .setContentText("We are tracking your location to allow your well-wishers to track you.")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        startForeground(Constants.ACTIVE_NOTIFICATION_ID, notification);
        //handler.post(periodicLocationFetch);

        if(counter <= 1000){
            if(periodicLocationCallback == null){
                periodicLocationUpdateBegin();
            }
        }

        int returnCode = super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void periodicLocationUpdateBegin(){
        periodicLocationCallback = locationProvider.startLocationUpdates(this.workflowContext, new IPeriodicLocationCallback() {
            @Override
            public void onPeriodicLocationUpdateReceived(Location location) {
                // TODO: Add logic to post values to server.
                postCurrentStatus("Location update rec:" + counter);
                counter++;
                logger.LogInformation(LocationHelper.getLocationString(location), workflowContext.getWorkflowId());

                if(counter >= 1000){
                    if(periodicLocationCallback != null){
                        locationProvider.stopLocationUpdates(workflowContext, periodicLocationCallback);
                        periodicLocationCallback = null;
                    }

                    logger.LogInformation("Stopping service", workflowContext.getWorkflowId());
                    postCurrentStatus("Stopping service");
                    stopForeground(true);
                    stopSelf();
                }
            }

            @Override
            public void onLocationCheckLogEventReceived(String message) {
                postCurrentStatus("Location Check log event received" + counter);
                logger.LogInformation("Location Check log event received", workflowContext.getWorkflowId());
                counter++;
            }

            @Override
            public void onFailedLocationUpdateStart(String failureReason) {
                postCurrentStatus("Location failed update received" + counter);
                logger.LogInformation("Location failed update received", workflowContext.getWorkflowId());
                counter++;
            }
        });
    }

    private void postCurrentStatus(String status) {
        if(lastButFourStatus != null){
            lastButFiveStatus = lastButFourStatus;
        }

        if(lastButThreeStatus != null){
            lastButFourStatus = lastButThreeStatus;
        }

        if(lastButTwoStatus != null){
            lastButThreeStatus = lastButTwoStatus;
        }

        if(lastButOneStatus != null){
            lastButTwoStatus = lastButOneStatus;
        }

        if(currentStatus != null){
            lastButOneStatus = currentStatus;
        }

        currentStatus = status + Calendar.getInstance().getTime().toString();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        postCurrentStatus("On Task removed called.");
        logger.LogInformation("On Task removed called.", this.workflowContext.getWorkflowId());
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);

        postCurrentStatus("OnCreate called");
        this.workflowContext = new WorkflowContext(FetchCurrentLocationService.class.getName(), WorkflowSourceType.Service_Create);
        logger.LogInformation("Service Created", this.workflowContext.getWorkflowId());
    }

    @Override
    public void onDestroy() {
        postCurrentStatus("OnDestroy Called");
        logger.LogInformation("Service destroyed", this.workflowContext.getWorkflowId());
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        postCurrentStatus("OnBind called");
        logger.LogInformation("OnBind called.", workflowContext.getWorkflowId());
        return this.fetchCurrentLocationServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logger.LogInformation("OnUnbind called.", workflowContext.getWorkflowId());
        postCurrentStatus("OnUnBind called");
        return super.onUnbind(intent);
    }

    public String fetchStatus(){
        return this.currentStatus + "\n" + this.lastButOneStatus +
                "\n" + this.lastButTwoStatus + "\n" + this.lastButThreeStatus +
                "\n" + this.lastButFourStatus + "\n" + this.lastButFiveStatus;
    }

    private void fetchLocation() {
        if (null != locationProvider) {
            this.locationProvider.getCurrentLocation(this.workflowContext, new ILocationCallback() {
                @Override
                public void onLocationCheckLogEventReceived(String message) {
                    logger.LogInformation(message, workflowContext.getWorkflowId());
                }

                @Override
                public void onCurrentLocationRequestComplete(Location location) {
                    logger.LogInformation(LocationHelper.getLocationString(location), workflowContext.getWorkflowId());
                }

                @Override
                public void onLocationRequestFailure(String failureReason) {
                    logger.LogInformation(failureReason, workflowContext.getWorkflowId());
                }

                @Override
                public void onRequestEnableGpsSettingRequired(ResolvableApiException ex) {
                    logger.LogInformation(Utility.FlattenException(ex), workflowContext.getWorkflowId());
                }
            });
        }
    }
}
