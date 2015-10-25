package com.byteshaft.itourguide;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class ReceiveTransitionsIntentService extends IntentService {

    public static ArrayList<String[]> geofenceNames;

    protected static final String TAG = "GeofenceTransitionsIS";
    public ReceiveTransitionsIntentService() {
        super("test");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    triggeringGeofences
            );

            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            Log.e(TAG, getString(geofenceTransition));
        }
    }

    private String getGeofenceTransitionDetails(
            List<Geofence> triggeringGeofences) {


        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(",  ", triggeringGeofencesIdsList);
        return triggeringGeofencesIdsString;
    }

    private void sendNotification(String notificationDetails) {
        String[] array = notificationDetails.split(",");
        Intent notificationIntent = null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (array.length == 1) {
            notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);
            builder.setContentTitle("Place Nearby: " + notificationDetails);
            builder.setContentText("Touch to navigate.");
            for (int i = 0; i < DataVariables.array.length; i++) {
                if (TextUtils.equals(notificationDetails.trim(), DataVariables.array[i][0].trim())) {
                    LocationService.targetLat = Double.parseDouble(DataVariables.array[i][2]);
                    LocationService.targetLon = Double.parseDouble(DataVariables.array[i][3]);
                }
            }
        } else if (array.length > 1) {
            ArrayList<String[]> geofenceNames = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                    geofenceNames.add(array);
                }
            PlacesDialogActivity.filteredLocationsForDialog = geofenceNames;
            notificationIntent = new Intent(getApplicationContext(), PlacesDialogActivity.class);
            builder.setContentTitle("Multiple places in radius");
            builder.setContentText("Touch to show.");
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.mipmap.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.GRAY)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    public class notificationClick extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.i("String", "Click");
        }
    }

}