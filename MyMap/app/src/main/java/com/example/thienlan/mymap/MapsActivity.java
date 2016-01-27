package com.example.thienlan.mymap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.thienlan.mymap.Code.GPSTracker;
import com.example.thienlan.mymap.Code.GetJson;
import com.example.thienlan.mymap.Code.SaveLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public GoogleMap mMap;
    GPSTracker gpsTracker = null;
    String address, province, ward, city, country;
    PlaceAutocompleteFragment autocompleteFragment;
    ImageButton ib, ib2;
    LatLng dest, src;
    String findString = null;
    Marker myMaker, findMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ib = (ImageButton) findViewById(R.id.imageButton);
        ib2 = (ImageButton) findViewById(R.id.imageButton2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsTracker = new GPSTracker(this);
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                try {
                    findMarker.remove();
                } catch (Exception e) {
                }
                dest = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));
                findMarker = AddMarker(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
                findString = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                Log.d("Place: ", status.getStatus().toString());
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsTracker.canGetLocation()) {
                    if (findString != null) {
                        mMap.clear();
                        src = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        myMaker = AddMarker("You are here", gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        findMarker=AddMarker(findString, dest.latitude, dest.longitude);
                        findMarker.showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(src, 15));
                        new GetJson(MapsActivity.this, dest, src, mMap).GetArray();
                    } else {
                        mMap.clear();
                        Toast.makeText(MapsActivity.this, "Please select place", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });

        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListDialog(new SaveLocation(MapsActivity.this).LoadFile());
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
                    myMaker = AddMarker("You are here", gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    myMaker.showInfoWindow();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }
                return false;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals("markerx")) {
                    ShowDialog(marker.getSnippet());
                }
                return false;
            }
        });
    }

    /* Add Marker on map
     * place: add marker's title */
    public Marker AddMarker(String place, double lat, double lng) {
        Marker temp = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //Check item null
            address = addresses.get(0).getAddressLine(0) != null ? addresses.get(0).getAddressLine(0) : "";
            city = addresses.get(0).getLocality() != null ? addresses.get(0).getLocality() : "";
            province = addresses.get(0).getSubLocality() != null ? addresses.get(0).getSubLocality() : "";
            ward = addresses.get(0).getSubAdminArea() != null ? addresses.get(0).getSubAdminArea() : "";
            country = addresses.get(0).getCountryName() != null ? addresses.get(0).getCountryName() : "";

            /*Add a marker in Sydney and move the camera*/
            LatLng sydney = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            temp = mMap.addMarker(new MarkerOptions().position(sydney)
                    .title(place).snippet(address + " " + province + " " + ward + " " + city + " " + country)
                    .draggable(true));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void ShowDialog(final String location) {
        final AlertDialog.Builder mydialog = new AlertDialog.Builder(MapsActivity.this);
        mydialog.setTitle("Save location?");
        mydialog.setMessage(location);

        mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Savefile();
                new SaveLocation(MapsActivity.this).WriteFile(location);
            }
        });

        mydialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = mydialog.create();
        alert.show();
    }

    public void ShowListDialog(final String[] list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your saved location");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autocompleteFragment.setText(list[which]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            myMaker.remove();
        }
        catch (Exception e){
        }

        myMaker = AddMarker("You are here", gpsTracker.getLatitude(), gpsTracker.getLongitude());
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

}