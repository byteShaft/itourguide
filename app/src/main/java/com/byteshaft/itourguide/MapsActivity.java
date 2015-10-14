package com.byteshaft.itourguide;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    Button satelliteButton;
    Button normalButton;
    LatLng start;
    LatLng end;
    LatLng wayPoint;
    LatLng targetLocation;
    LatLng currentLocation;
    RoutingListener routingListener;
    public static MarkerOptions a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MainActivity.isMapActivityOpen = true;
        LocationService locationService = new LocationService(AppGlobals.getContext());
        routingListener = new RoutingListener() {
            @Override
            public void onRoutingFailure() {

            }

            @Override
            public void onRoutingStart() {

            }

            @Override
            public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
                PolylineOptions polyoptions = new PolylineOptions();
                polyoptions.color(Color.BLUE);
                polyoptions.width(10);
                polylineOptions.zIndex(102);
                polyoptions.addAll(polylineOptions.getPoints());
                mMap.addPolyline(polyoptions);
            }

            @Override
            public void onRoutingCancelled() {

            }
        };
        start = new LatLng(LocationService.latitude, LocationService.longitude);
        end = new LatLng(MainActivity.finalLat, MainActivity.finalLon);
        satelliteButton = (Button) findViewById(R.id.satelliteButton);
        normalButton = (Button) findViewById(R.id.normalButton);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        targetLocation = new LatLng(MainActivity.finalLat, MainActivity.finalLon);
        currentLocation = new LatLng(LocationService.latitude, LocationService.longitude);
        a = new MarkerOptions().position(targetLocation);
        mMap.addMarker(a);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(targetLocation));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.finalLat, MainActivity.finalLon), 16.0f));
        Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.WALKING)
                .withListener(routingListener).waypoints(start, end).build();
        routing.execute();
        satelliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
    }
}
