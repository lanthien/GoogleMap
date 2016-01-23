package com.example.thienlan.mymap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.thienlan.mymap.Code.GPSTracker;
import com.example.thienlan.mymap.Code.HttpConnection;
import com.example.thienlan.mymap.Code.PathJSONParser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    public GoogleMap mMap;
    protected double longtitude, latitude;
    GPSTracker gpsTracker = null;
    LocationManager locationManager;
    String address, province, ward, city, country;
    PlaceAutocompleteFragment autocompleteFragment;
    final String TAG = "PathGoogleMapActivity";
    ImageButton ib;
    LatLng dest, src;
    JsonObject result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ib = (ImageButton) findViewById(R.id.imageButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        gpsTracker = new GPSTracker(this);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                latitude = place.getLatLng().latitude;
                longtitude = place.getLatLng().longitude;
                dest = new LatLng(latitude, longtitude);
                AddMarker(place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Log.d("Place: ", status.getStatus().toString());
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng src = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                LatLng dest= new LatLng(latitude,longtitude);
                new DrawDirection(MapsActivity.this,mMap,src,dest).getJson();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        src = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            /*Set Location button enable*/
            mMap.setMyLocationEnabled(true);
        }
        // Polylines are useful for marking paths and routes on the map.



        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longtitude = gpsTracker.getLongitude();
                    AddMarker("You are here");
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }
                return false;
            }
        });

    }

    /* Add Marker on map
     * place: add marker's title */
    public void AddMarker(String place) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //Check item null
            address = addresses.get(0).getAddressLine(0) != null ? addresses.get(0).getAddressLine(0) : "";
            city = addresses.get(0).getLocality() != null ? addresses.get(0).getLocality() : "";
            province = addresses.get(0).getSubLocality() != null ? addresses.get(0).getSubLocality() : "";
            ward = addresses.get(0).getSubAdminArea() != null ? addresses.get(0).getSubAdminArea() : "";
            country = addresses.get(0).getCountryName() != null ? addresses.get(0).getCountryName() : "";

            /*Add a marker in Sydney and move the camera*/
            LatLng sydney = new LatLng(latitude, longtitude);
            mMap.addMarker(new MarkerOptions().position(sydney)
                    .title(place).snippet(address + " " + province + " " + ward + " " + city + " " + country + "\nLatLng: " + latitude + " " + longtitude)
                    .draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMapsApiDirectionsUrl() {
//        String waypoints = "waypoints=optimize:true|"
//                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
//                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
//                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
//                + WALL_STREET.longitude;

        String waypoints = "waypoints=optimize:true|"
                + dest.latitude + "," + dest.longitude
                + "|" + "|" + src.latitude + ","
                + src.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/geocode/"
                + output + "?" + params;
        return url;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }
            mMap.addPolyline(polyLineOptions);
        }
    }
}