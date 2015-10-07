package com.byteshaft.itourguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button acquireLocationButton;
    LocationService locationService;
    LocationHelpers locationHelpers;
    static ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<String[]> filteredLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHelpers = new LocationHelpers(MainActivity.this);
        acquireLocationButton = (Button) findViewById(R.id.location_button);
        listView = (ListView) findViewById(R.id.lv_main);
        arrayAdapter = new PlaceList(AppGlobals.getContext(), R.layout.row, filteredLocations);
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

    class PlaceList extends ArrayAdapter<String> {

        int mResource;
        public PlaceList(Context context, int resource, ArrayList objects) {
            super(context, resource, objects);
            mResource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.description = (TextView) convertView.findViewById(R.id.tv_description);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(filteredLocations.get(position)[0]);
            holder.description.setText(filteredLocations.get(position)[1]);
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView name;
        public TextView description;
    }
}
