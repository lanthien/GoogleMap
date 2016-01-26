package com.example.thienlan.mymap.Code;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ThienLan on 27/01/2016.
 */
public class GetJson {

    Context context;
    LatLng start,end;
    ArrayList<LatLng> patch;
    GoogleMap googleMap;
    double lat,lng;

    public GetJson(Context context,LatLng start, LatLng end,GoogleMap googleMap){
        this.context = context;
        this.start=start;
        this.end=end;
        this.googleMap=googleMap;
    }

    public void GetArray()
    {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";

        patch = new ArrayList<LatLng>();

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray jsonArray = result.getAsJsonArray("routes")
                                                    .get(0).getAsJsonObject().getAsJsonArray("legs")
                                                    .get(0).getAsJsonObject().getAsJsonArray("steps");
                        for(int i=0;i<jsonArray.size();i++)
                        {
                            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject()
                                    .getAsJsonObject("start_location")
                                    .getAsJsonObject();
                            lat = jsonObject.get("lat").getAsDouble();
                            lng = jsonObject.get("lng").getAsDouble();
                            patch.add(new LatLng(lat, lng));

                            jsonObject = jsonArray.get(i).getAsJsonObject()
                                                                    .getAsJsonObject("end_location")
                                                                    .getAsJsonObject();
                            lat = jsonObject.get("lat").getAsDouble();
                            lng = jsonObject.get("lng").getAsDouble();
                            Log.d("Test",lat+", "+lng);
                            patch.add(new LatLng(lat, lng));
                        }

                        googleMap.addPolyline(new PolylineOptions().geodesic(true).color(Color.RED).width(3).addAll(patch));
                        //new DrawPatch(patch).execute();
                    }
                });
    }
}
