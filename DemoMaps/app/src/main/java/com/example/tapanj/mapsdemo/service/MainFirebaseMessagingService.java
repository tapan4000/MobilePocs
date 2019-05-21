package com.example.tapanj.mapsdemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceEncryptionMetadaStore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        // Test Results on sending an FCM notification
        // 1) When notification with data is sent and if the app is in foreground, the data can be handled in the firebasemessagingservice onMessageReceived method
        // 2) When notification with data is sent and if the app is in background, then a push notification is displayed and clicking on the notification, user is taken to the app.
        // 3) When data alone is sent and app is in the background, then data can be handled using firebasemessagingservice.
        // 4) When data alone is sent and app is in the foreground, then data can be handled using firebasemessagingservice.
        SharedPreferences preferences = getSharedPreferences(SharedPreferenceConstants.SharedPreferenceFileName, Context.MODE_PRIVATE);

        postCurrentStatus("Message Received");
        String from = remoteMessage.getFrom();
        Map<String, String> payload = remoteMessage.getData();
        String content = null;
        for(Map.Entry<String, String> entry: payload.entrySet()){
            content += entry.getKey() + ":" + entry.getValue() + ",";
        }

        StoreSharedPreference(preferences, SharedPreferenceConstants.ServiceCallTestMessage, content);

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

    private void StoreSharedPreference(SharedPreferences sharedPreferences, String keyName, String message) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String serializedObject = message;

        sharedPreferenceEditor.putString(keyName, serializedObject);
        sharedPreferenceEditor.commit();
    }


}
