package com.byteshaft.itourguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button acquireLocationButton;
    LocationService locationService;
    LocationHelpers locationHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acquireLocationButton = (Button) findViewById(R.id.location_button);
        acquireLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelpers = new LocationHelpers(getApplicationContext());
                if (!locationHelpers.isAnyLocationServiceAvailable()) {
                    Toast.makeText(MainActivity.this, "Please enable Location Service from " +
                            "Android System Settings and set it to High Accuracy", Toast.LENGTH_SHORT).show();
                } else {
                    locationService = new LocationService(getApplicationContext());
                }
            }
        });
    }
}
