package com.byteshaft.itourguide;

import android.content.Context;

import com.google.android.gms.location.GeofenceStatusCodes;

public class GeofenceErrorMessages {
    private GeofenceErrorMessages() {}
    public static String getErrorString(Context context, int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return ("Geofence Not Available");
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return ("Too Many Geofences");
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return ("Too Many Pending Intents");
            default:
                return ("Unknown Geofencing Error");
        }
    }
}
