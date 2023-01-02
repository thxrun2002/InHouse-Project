package com.example.quikcar;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.util.List;

public class useractivity extends AppCompatActivity implements OnMapReadyCallback {
    int REQUEST_LOCATION = 99;
    boolean permission ;
    GoogleMap gmap;
    EditText locationText;
    String provider;
    ImageView searchimg;
    private Geocoder geocoder;
    LocationManager locationManager;
    MyLocationListener mylistener;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location;
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useractivity);
        locationText = findViewById(R.id.locationText);
        searchimg = findViewById(R.id.search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        int REQUEST_LOCATION = 99;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(useractivity.this, new String[]
                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

         location = locationManager.getLastKnownLocation(provider);

        mylistener = new MyLocationListener();

        if (location != null) {

            mylistener.onLocationChanged(location);
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        if (ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(provider, 200, 5, mylistener);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng currlatlng = new LatLng(location.getLatitude(),location.getLongitude());
        latitude =  currlatlng.latitude;
        longitude = currlatlng.longitude;
        MarkerOptions markerOptions = new MarkerOptions().position(currlatlng).title("Current Location");
        gmap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currlatlng,15);
        gmap.animateCamera(cameraUpdate);
        Log.d("LATITUDE","${latitude}");
        check();
    }
    private void check(){
        if (ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(useractivity.this, new String[]
                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Toast.makeText(this, "LOCATION ALLOWED!", Toast.LENGTH_SHORT).show();
                check();
        }
        else {
            permission = true;

        }
    }
    public void finddriver(View v){
        Intent intent = new Intent(useractivity.this,viewDriver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        v.getContext().startActivity(intent);
    }
    public void searchLocation(View v){
            String location = locationText.getText().toString();
            if(location.equals("")|| location.equals(" ")){
                Toast.makeText(useractivity.this, "ENTER A LOCATION", Toast.LENGTH_SHORT).show();
            }
            else{
                geocoder = new Geocoder(this);
                try {
                    List<Address> addressList = geocoder.getFromLocationName(location,1);
                    Address address = addressList.get(0);
                    if(addressList.size() > 0){
                        LatLng place = new LatLng(address.getLatitude(),address.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(place).title(address.getLocality());
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        gmap.clear();
                        gmap.addMarker(markerOptions);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place,15);
                        gmap.animateCamera(cameraUpdate);
                    }
                    else{
                        Toast.makeText(this, "Location Not Found! Retry another location!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    private class MyLocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(useractivity.this, "" + location.getLatitude() + location.getLongitude(),
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(useractivity.this, provider + "'s status changed to " + status + "!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(useractivity.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(useractivity.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}