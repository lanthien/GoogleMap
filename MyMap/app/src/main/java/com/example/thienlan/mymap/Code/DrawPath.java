package com.example.thienlan.mymap.Code;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by ThienLan on 27/01/2016.
 */
public class DrawPath extends AsyncTask<Void, Void,Void> {

    ArrayList<LatLng> listLatLng;
    GoogleMap googleMap;

    public DrawPath(ArrayList<LatLng> listLatLng, GoogleMap googleMap)
    {
        this.listLatLng = listLatLng;
        this.googleMap = googleMap;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        googleMap.addPolyline(new PolylineOptions().color(Color.RED).width(5).addAll(listLatLng));
    }
}
