package com.byteshaft.itourguide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Button acquireLocationButton;
    Button cancelButtonDialog;
    Button okButtonDialog;
    EditText radiusEditTextOne;
    EditText radiusEditTextTwo;
    public static ImageView imageViewName;
    LocationService locationService;
    LocationHelpers locationHelpers;
    static ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<String[]> filteredLocations;
    public static MainActivity instance;
    public static Double finalLat;
    public static Double finalLon;
    SharedPreferences sharedPreferences;
    public static boolean isMapActivityOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        instance = this;
        locationHelpers = new LocationHelpers(MainActivity.this);
        imageViewName = (ImageView) findViewById(R.id.iv_name);
        acquireLocationButton = (Button) findViewById(R.id.button_acquire_location);
        listView = (ListView) findViewById(R.id.lv_main);
        arrayAdapter = new PlaceList(this, R.layout.row, filteredLocations);
        if (filteredLocations != null) {
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), String.valueOf(filteredLocations.get(position)[3]), Toast.LENGTH_LONG).show();
                    finalLat = Double.parseDouble(filteredLocations.get(position)[2]);
                    finalLon = Double.parseDouble(filteredLocations.get(position)[3]);
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
            });
        }
        if (!locationHelpers.playServicesAvailable()) {
            locationHelpers.showGooglePlayServicesError(MainActivity.this);
        }
        acquireLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewName.setVisibility(View.GONE);
                imageViewName.setImageResource(R.mipmap.name_aquiring_location);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.activity_settings_dialog);
            dialog.setCancelable(false);

            radiusEditTextOne = (EditText) dialog.findViewById(R.id.et_radius_one);
            radiusEditTextOne.setText(String.format("%d", sharedPreferences.getInt("radius_one", 10)));
            radiusEditTextTwo = (EditText) dialog.findViewById(R.id.et_radius_two);
            radiusEditTextTwo.setText(String.format("%d", sharedPreferences.getInt("radius_two", 2)));



            cancelButtonDialog = (Button) dialog.findViewById(R.id.button_dialog_cancel);
            cancelButtonDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            okButtonDialog = (Button) dialog.findViewById(R.id.button_dialog_ok);
            okButtonDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (radiusEditTextOne.getText().toString().trim().length() < 1
                            || radiusEditTextTwo.getText().toString().trim().length() < 1) {
                        Toast.makeText(getApplicationContext(), "One or more fields are empty", Toast.LENGTH_SHORT).show();
                    } else {
                        int radiusOne = Integer.parseInt(radiusEditTextOne.getText().toString().trim());
                        sharedPreferences.edit().putInt("radius_one", radiusOne).apply();

                        int radiusTwo = Integer.parseInt(radiusEditTextTwo.getText().toString().trim());
                        sharedPreferences.edit().putInt("radius_two", radiusTwo).apply();

                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
