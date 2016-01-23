package com.example.thienlan.mymap;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by ThienLan on 22/01/2016.
 */
public class DrawDirection {

    Context context;
    public static JsonObject node;
    JsonArray jsonArray;
    GoogleMap googleMap;
    LatLng src, dest;

    public DrawDirection(Context context,GoogleMap googleMap, LatLng src, LatLng dest)
    {
        this.context=context;
        this.googleMap=googleMap;
        this.src = src;
        this.dest=dest;
    }

    public void getJson()
    {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
            + "origin=" + src.latitude + "," + src.longitude
            + "&destination=" + dest.latitude + "," + dest.longitude
            + "&sensor=false&units=metric&mode=driving";

        node = new JsonObject();


        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        node = result;
                        jsonArray=node.getAsJsonArray("geocoded_waypoints");
                    }
                });
        //jsonArray=node.getAsJsonArray("abridged_cast");

    }

    private class Mytask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}