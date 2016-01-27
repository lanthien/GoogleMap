package com.example.thienlan.mymap.Code;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by ThienLan on 27/01/2016.
 */
public class DrawPath extends AsyncTask<Void, Void,Void> {

    ArrayList<LatLng> listLatLng;
    GoogleMap googleMap;
    String time;
    Context context;
    Marker markerx;

    public DrawPath(ArrayList<LatLng> listLatLng, GoogleMap googleMap,String time,Context context)
    {
        this.listLatLng = listLatLng;
        this.googleMap = googleMap;
        this.time=time;
        this.context =context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        googleMap.addPolyline(new PolylineOptions().color(Color.RED).width(5).addAll(listLatLng));
        try
        {
            markerx.remove();
        }
        catch (Exception e){

        }
        markerx = googleMap.addMarker(new MarkerOptions().position(listLatLng.get(listLatLng.size()/2)).title("time travel: "+ time).visible(true));
        markerx.showInfoWindow();
        Toast.makeText(context,time,Toast.LENGTH_SHORT).show();

    }
}
