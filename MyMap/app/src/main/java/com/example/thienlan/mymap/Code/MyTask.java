package com.example.thienlan.mymap.Code;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

/**
 * Created by ThienLan on 24/01/2016.
 */
public class MyTask extends AsyncTask<Void, Void,Void> {

    GoogleMap googleMap;
    JsonArray jsonArray;

    public MyTask(GoogleMap googleMap, JsonArray jsonArray)
    {
        this.googleMap=googleMap;
        this.jsonArray=jsonArray;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("aaaaaaaa",jsonArray.size()+"");
        return null;
    }
}
