package com.byteshaft.itourguide;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class AppGlobals extends Application {

    private static Context sContext;
    public static boolean locationServiceActive;
    public static LatLng targetLocation;

    static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
