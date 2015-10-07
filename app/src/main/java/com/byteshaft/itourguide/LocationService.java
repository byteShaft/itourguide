package com.byteshaft.itourguide;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationService extends ContextWrapper implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public GoogleApiClient mGoogleApiClient;
    public Location mLocation;
    private int mLocationChangedCounter = 0;
    private LocationRequest mLocationRequest;
    private CountDownTimer mTimer;
    static double latitude;
    static double longitude;
    public static Double lat2;
    public static Double lng2;

    public LocationService(Context context) {
        super(context);
        connectingGoogleApiClient();
        locationTimer().start();
    }

    public void connectingGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        long INTERVAL = 0;
        long FASTEST_INTERVAL = 0;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationService() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        locationTimer().cancel();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location", "onLocationChanged CALLED..." + mLocationChangedCounter);
        mLocationChangedCounter++;

        if (mLocationChangedCounter == 3) {
            mLocation = location;
            String lat = String.valueOf(mLocation.getLatitude());
            String lon = String.valueOf(mLocation.getLongitude());
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            lat2 = mLocation.getLatitude();
            lng2 = mLocation.getLongitude();
            Log.i("Location", lat2 + ", " + lng2);
            Toast.makeText(getApplicationContext(), "Location acquired", Toast.LENGTH_SHORT).show();
            stopLocationService();
            mLocationChangedCounter = 0;

            ArrayList<String[]> storedLocations = new ArrayList<>();

            for (int i = 0; i < DataVariables.array.length; i++) {
                Double storedLat = Double.parseDouble(DataVariables.array[i][2]);
                Double storedLon = Double.parseDouble(DataVariables.array[i][3]);
                if (distance(storedLat, storedLon, LocationService.lat2, LocationService.lng2) < 2) {
                    Log.i("In Range", DataVariables.array[i][0]);
                    storedLocations.add(DataVariables.array[i]);
                }
            }
            MainActivity.filteredLocations = storedLocations;
            MainActivity.instance.recreate();
//            MainActivity.listView.setAdapter(MainActivity.arrayAdapter);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public CountDownTimer locationTimer() {

        if (mTimer == null) {
            mTimer = new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("Location", "Timer: " + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    if (mGoogleApiClient.isConnected()) {
                        stopLocationService();
                        Log.i("Location", "Location cannot be acquired.");
                            /* TODO: Implement Response */
                    }
                }
            };
        }
        return mTimer;
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }
}
