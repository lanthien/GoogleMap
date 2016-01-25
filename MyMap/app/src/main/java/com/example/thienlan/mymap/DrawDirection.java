package com.example.thienlan.mymap;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.thienlan.mymap.Code.ReadWriteFile;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ThienLan on 22/01/2016.
 */
public class DrawDirection {

    Context context;
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

        jsonArray =new JsonArray();

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        //get routes[] GSonArray
                        jsonArray = result.getAsJsonArray("routes");

                        //get {"steps":[]} string
                        String[] x = jsonArray.toString().split("\"steps\":");
                        String[] t = x[1].split(",\"via_waypoint\"");
                        t[0]="{\"steps\":"+t[0]+"}";

                        //Parse to JSON, JSONArray
                        try {
                            JSONObject json = new JSONObject(t[0]);
                            JSONArray jsonarray = json.getJSONArray("steps");
                            Log.d("Test2: ",jsonarray.length()+"");
                        } catch (JSONException e1) {
                            Log.d("Test2: ","0");
                        }
                    }
                });

        //jsonArray=node.getAsJsonArray("abridged_cast");

    }
}