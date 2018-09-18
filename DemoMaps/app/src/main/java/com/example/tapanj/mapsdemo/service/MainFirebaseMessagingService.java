package com.example.tapanj.mapsdemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.common.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

public class MainFirebaseMessagingService extends FirebaseMessagingService {
    private String currentStatus;
    private String lastButOneStatus;
    private String lastButTwoStatus;
    private String lastButThreeStatus;
    private String lastButFourStatus;
    private String lastButFiveStatus;
    private static String consolidatedValue = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        postCurrentStatus("Message Received");
        String from = remoteMessage.getFrom();
        Map<String, String> payload = remoteMessage.getData();
        if(remoteMessage.getNotification() != null){
            String notificatioBody = remoteMessage.getNotification().getBody();
        }

        Intent notificationIntent = new Intent(this, GroupListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String receivedValue = payload.values().toArray()[0].toString();

        if(receivedValue == "0"){
            consolidatedValue = "";
        }

        consolidatedValue = consolidatedValue + receivedValue;

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentTitle(consolidatedValue)
                .setContentText("Firebase")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
        
        Intent intent = new Intent(this, FetchCurrentLocationService.class);
        //stopService(intent);
        startService(intent);
        super.onMessageReceived(remoteMessage);
}

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
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
}
