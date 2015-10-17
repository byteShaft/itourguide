package com.byteshaft.itourguide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

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
    ImageButton mapViewButton;
    LatLng start;
    LatLng end;
    LatLng targetLocation;
    LatLng currentLocation;
    RoutingListener routingListener;
    public static MarkerOptions a;
    Boolean mapViewSatellite = false;
    static boolean isMapsActivityOpened = false;
    static Marker currentLocationMarker;
    static Marker targetLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isMapsActivityOpened = true;
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
                polyoptions.color(Color.RED);
                polyoptions.width(15);
                polylineOptions.zIndex(102);
                polyoptions.addAll(polylineOptions.getPoints());
                mMap.addPolyline(polyoptions);
            }

            @Override
            public void onRoutingCancelled() {

            }
        };
        start = new LatLng(LocationService.latitude, LocationService.longitude);
        end = new LatLng(ListActivity.finalLat, ListActivity.finalLon);
        mapViewButton = (ImageButton) findViewById(R.id.button_map_view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        targetLocation = new LatLng(ListActivity.finalLat, ListActivity.finalLon);
        currentLocation = new LatLng(LocationService.latitude, LocationService.longitude);
        a = new MarkerOptions().position(targetLocation);
        targetLocationMarker = mMap.addMarker(a);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(targetLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ListActivity.finalLat, ListActivity.finalLon), 16.0f));
        isMapsActivityOpened = true;
        Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.WALKING)
                .withListener(routingListener).waypoints(start, end).build();
        routing.execute();
        mapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapViewSatellite) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapViewButton.setImageResource(R.mipmap.ic_satellite);
                    mapViewSatellite = false;
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mapViewButton.setImageResource(R.mipmap.ic_map_normal);
                    mapViewSatellite = true;
                }
            }
        });
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(LocationService.currentLocationForMap).title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        a.position(LocationService.currentLocationForMap);
    }
}