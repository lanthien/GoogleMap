package com.example.thienlan.mymap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.thienlan.mymap.Code.GPSTracker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    protected double longtitude, latitude;
    GPSTracker gpsTracker = null;
    LocationManager locationManager;
    String address, province, ward, city, country;
    PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                latitude = place.getLatLng().latitude;
                longtitude = place.getLatLng().longitude;
                AddMarker(place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Log.d("Place: ", status.getStatus().toString());
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,//update per 3s
                    1,//update per 1m
                    new MapsActivity()
            );
        }

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
        gpsTracker = new GPSTracker(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            /*Set Location button enable*/
            mMap.setMyLocationEnabled(true);
        }

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

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        AddMarker("You are here");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
}