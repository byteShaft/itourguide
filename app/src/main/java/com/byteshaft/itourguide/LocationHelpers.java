package com.byteshaft.itourguide;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.LocationManager;

public class LocationHelpers extends ContextWrapper {

    public LocationHelpers(Context base) {
        super(base);
    }

    public boolean isAnyLocationServiceAvailable() {
        LocationManager locationManager = getLocationManager();
        return isGpsEnabled(locationManager) || isNetworkBasedGpsEnabled(locationManager);
    }

    private LocationManager getLocationManager() {
        return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean isGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkBasedGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER));
    }

}