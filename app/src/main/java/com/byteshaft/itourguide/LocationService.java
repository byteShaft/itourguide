package com.byteshaft.itourguide;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationService extends ContextWrapper implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public GoogleApiClient mGoogleApiClient;
    public Location mLocation;
    public static int mLocationChangedCounter = 0;
    private LocationRequest mLocationRequest;
    private CountDownTimer mTimer;
    static double latitude;
    static double longitude;
    public static double freshLatitude;
    public static double freshLongitude;
    public static LatLng currentLocationForMap;
    SharedPreferences sharedPreferences;
    private static LocationService instance;
    Marker currentLocationMarker;

    private LocationService(Context context) {
        super(context);
    }

    public static LocationService getInstance(Context context) {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
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
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        Log.i("Location", "onLocationChanged CALLED..." + mLocationChangedCounter);
        mLocationChangedCounter++;
            if (mLocationChangedCounter == 3) {
                locationTimer().cancel();
                MainActivity.imageViewName.setImageResource(R.mipmap.name_main);
                MainActivity.imageViewName.setVisibility(View.VISIBLE);
                mLocation = location;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                int radiusOne = sharedPreferences.getInt("radius_one", 10);
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                Toast.makeText(getApplicationContext(), "Location acquired", Toast.LENGTH_SHORT).show();

                ArrayList<String[]> storedLocations = new ArrayList<>();

                for (int i = 0; i < DataVariables.array.length; i++) {
                    Double storedLat = Double.parseDouble(DataVariables.array[i][2]);
                    Double storedLon = Double.parseDouble(DataVariables.array[i][3]);
                    if (distance(storedLat, storedLon, LocationService.latitude, LocationService.longitude) < radiusOne) {
                        Log.i("In Range", DataVariables.array[i][0]);
                        storedLocations.add(DataVariables.array[i]);
                    }
                }
                ListActivity.filteredLocations = storedLocations;
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        if (mLocationChangedCounter >= 3) {
            freshLatitude = location.getLatitude();
            freshLongitude = location.getLongitude();
            currentLocationForMap = new LatLng(freshLatitude, freshLongitude);
            drawMarker(location);
            }
        }

    private void drawMarker(Location location) {
        if (MapsActivity.isMapsActivityOpened) {
            System.out.println("Running..");
            if (currentLocationMarker != null) {
                MapsActivity.currentLocationMarker.remove();
            }
            currentLocationMarker = MapsActivity.mMap.addMarker(new MarkerOptions()
                    .position(currentLocationForMap)
                    .snippet("Lat:" + location.getLatitude() + " Lng:" + location.getLongitude())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("ME"));
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public CountDownTimer locationTimer() {

        if (mTimer == null) {
            mTimer = new CountDownTimer(120000, 1000) {
                int dummyNumber = 0;
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("Location", "Timer: " + millisUntilFinished / 1000);
                    dummyNumber++;
                    if ((dummyNumber % 2) == 0) {
                        MainActivity.acquireLocationButton.setRotation(0);
                        MainActivity.imageViewName.setVisibility(View.VISIBLE);
                    } else {
                        MainActivity.acquireLocationButton.setRotation(90);
                        MainActivity.imageViewName.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFinish() {
                    if (mGoogleApiClient.isConnected()) {
                        stopLocationService();
                        Toast.makeText(getApplicationContext(), "Current location cannot be acquired", Toast.LENGTH_SHORT).show();
                        MainActivity.imageViewName.setImageResource(R.mipmap.name_main);
                        MainActivity.imageViewName.setVisibility(View.VISIBLE);
                        MainActivity.acquireLocationButton.setClickable(true);
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