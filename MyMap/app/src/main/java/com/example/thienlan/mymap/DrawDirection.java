package com.example.thienlan.mymap;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.thienlan.mymap.Code.MyTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by ThienLan on 22/01/2016.
 */
public class DrawDirection {

    Context context;
    public static JsonObject node;
    JsonArray jsonArray;
    GoogleMap googleMap;
    LatLng src, dest;
    Gson gson;
    JsonParser jsonParser;
    ArrayList jsonObjList;

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
        gson = new Gson();
        jsonParser=new JsonParser();
        jsonArray = new JsonArray();

        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
//                            jsonObjList = gson.fromJson(result.getAsJsonArray("routes"),ArrayList.class);
//                            System.out.println("List Elements are  : "+result.get("steps").toString());
//                            System.out.println("List Elements are  : "+jsonObjList.toString());
                            jsonArray = result.getAsJsonArray("routes");
                            jsonObjList = gson.fromJson(jsonArray, ArrayList.class);
                            System.out.println("List Elements are  : "+jsonObjList.size());
                            String[] x = jsonObjList.get(0).toString().split(",");
                            for(String l : x){
                                System.out.println("my data: "+ l);
                            }
                            System.out.println("data: "+  jsonObjList.get(0).toString().split(","));
                        }
                        catch (Exception ex)
                        {
                            System.out.println("Error: "+ex.getMessage());
                        }
                    }
                });
        //jsonArray=node.getAsJsonArray("abridged_cast");

    }
}