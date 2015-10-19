package com.byteshaft.itourguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
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
    ImageButton facebookShareButton;
    ImageButton showCurrentLocationButton;
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
        facebookShareButton = (ImageButton) findViewById(R.id.button_share_fb);
        showCurrentLocationButton = (ImageButton) findViewById(R.id.button_current_location);
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
        facebookShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MapsActivity.this);
                alertDialogBuilder.setTitle("Facebook");
                alertDialogBuilder
                        .setMessage("Want to share your Current Location?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                shareAppLinkViaFacebook();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        showCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LocationService.currentLocationForMap, 16);
                mMap.animateCamera(cameraUpdate);
            }
        });
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(LocationService.currentLocationForMap).title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        a.position(LocationService.currentLocationForMap);
    }

    private void shareAppLinkViaFacebook() {
        String urlToShare = "https://maps.google.com/maps?q=" + LocationService.freshLatitude + "," + LocationService.freshLongitude;

        try {
            Intent intent1 = new Intent();
            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
            intent1.setAction("android.intent.action.SEND");
            intent1.setType("text/plain");
            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(intent1);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            Intent intent = new Intent(Intent.ACTION_SEND);
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }
}