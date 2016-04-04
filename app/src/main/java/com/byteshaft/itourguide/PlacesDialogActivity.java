package com.byteshaft.itourguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlacesDialogActivity extends Activity {

    ListView mListViewDialog;
    public static ArrayList<String[]> filteredLocationsForDialog;
    ArrayAdapter mArrayAdapterDialog;
    LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_dialog);
        mListViewDialog = (ListView) findViewById(R.id.lv_places_dialog);
        mArrayAdapterDialog = new PlaceListForDialog(this, R.layout.row, filteredLocationsForDialog);
        mListViewDialog.setAdapter(mArrayAdapterDialog);
        mListViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) ((TextView) view.findViewById(R.id.tv_name)).getText();
                for (int i = 0; i < DataVariables.array.length; i++) {
                    if (TextUtils.equals(s.trim(), DataVariables.array[i][0].trim())) {
                        AppGlobals.targetLocation = new LatLng(Double.parseDouble(DataVariables.array[i][2]), Double.parseDouble(DataVariables.array[i][3]));
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    class PlaceListForDialog extends ArrayAdapter<String> {

        int mResource;
        public PlaceListForDialog(Context context, int resource, ArrayList objects) {
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
            holder.name.setText(filteredLocationsForDialog.get(position)[position]);
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView name;
        public TextView description;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
