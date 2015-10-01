package com.byteshaft.itourguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button acquireLocationButton;
    LocationService locationService;
    LocationHelpers locationHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHelpers = new LocationHelpers(MainActivity.this);
        acquireLocationButton = (Button) findViewById(R.id.location_button);
        if (!locationHelpers.playServicesAvailable()) {
            locationHelpers.showGooglePlayServicesError(MainActivity.this);
        }
        acquireLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelpers = new LocationHelpers(getApplicationContext());
                if (locationHelpers.playServicesAvailable() && !locationHelpers.isAnyLocationServiceAvailable()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Location Service disabled");
                    alertDialog.setMessage("Want to enable?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    alertDialog.show();
                } else if (!locationHelpers.playServicesAvailable()) {
                    locationHelpers.showGooglePlayServicesError(MainActivity.this);
                } else {
                    locationService = new LocationService(getApplicationContext());
                }
            }
        });
    }
}
