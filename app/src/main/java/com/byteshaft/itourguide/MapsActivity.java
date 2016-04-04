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
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    ImageButton mapViewButton;
    ImageButton facebookShareButton;
    ImageButton showCurrentLocationButton;
    public static RoutingListener routingListener;
    public static MarkerOptions a;
    Boolean mapViewSatellite = false;
    public static boolean isMapReady;
    static boolean isMapsActivityOpened = false;
    static Marker currentLocationMarker;
    static Marker targetLocationMarker;
    LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationService = LocationService.getInstance(getApplicationContext());
        if (!AppGlobals.locationServiceActive) {
            mLocationService.connectingGoogleApiClient();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mapViewButton = (ImageButton) findViewById(R.id.button_map_view);
        facebookShareButton = (ImageButton) findViewById(R.id.button_share_fb);
        showCurrentLocationButton = (ImageButton) findViewById(R.id.button_current_location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        targetLocationMarker = MapsActivity.mMap.addMarker(new MarkerOptions()
                .position(AppGlobals.targetLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Target"));
        CameraUpdate target = CameraUpdateFactory.newLatLngZoom(
                AppGlobals.targetLocation, 16.0f);
        mMap.animateCamera(target);
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
                if (LocationService.mLocationChangedCounter > 0) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LocationService.currentLocationForMap, 16);
                    mMap.animateCamera(cameraUpdate);
                } else {
                    Toast.makeText(AppGlobals.getContext(), "Location Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AppGlobals.locationServiceActive) {
            mLocationService.stopLocationService();
        }
        isMapReady = false;
        isMapsActivityOpened = false;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMapsActivityOpened = true;
    }

    public static void showRoute() {
        Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.WALKING)
                .withListener(routingListener).waypoints(LocationService.currentLocationForMap, AppGlobals.targetLocation).build();
        routing.execute();
    }
}