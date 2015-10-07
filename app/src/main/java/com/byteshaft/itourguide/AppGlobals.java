package com.byteshaft.itourguide;

import android.app.Application;
import android.content.Context;

public class AppGlobals extends Application {

    private static Context sContext;

    static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
