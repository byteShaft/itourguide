package com.byteshaft.itourguide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    static ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<String[]> filteredLocations;
    LocationService locationService;
    boolean itemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_activity);
        locationService = LocationService.getInstance(getApplicationContext());

        listView = (ListView) findViewById(R.id.list);
        arrayAdapter = new PlaceList(this, R.layout.row, filteredLocations);
        if (filteredLocations != null) {
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemClick = true;
                    AppGlobals.targetLocation = new LatLng(Double.parseDouble(filteredLocations.get(position)[2]),
                            Double.parseDouble(filteredLocations.get(position)[3]));
                    startActivity(new Intent(AppGlobals.getContext(), MapsActivity.class));
                }
            });
        }
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
    public void onBackPressed() {
        if (locationService.mGoogleApiClient.isConnected()) {
            locationService.stopLocationService();
            MainActivity.acquireLocationButton.setClickable(true);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationService.mGoogleApiClient.isConnected() && !itemClick) {
            locationService.stopLocationService();
        }
        finish();
    }
}