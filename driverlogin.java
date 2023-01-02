package com.example.quikcar;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class driverlogin extends AppCompatActivity {
    public driverlogin() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log  = findViewById(R.id.loginButton);
        setContentView(R.layout.activity_driverlogin);
    }
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    Button n,o,ava,notava,log;
    TextView heading, login,driverText;
    EditText phone, password;
    FirebaseFirestore db;
    String ID,dphone, dpass;
    public void newclick(View v){
        Intent i = new Intent(this,driveractivity.class);
        startActivity(i);
    }
    public void oldclick(View v){
        n= findViewById(R.id.NEW);
        o = findViewById(R.id.OLD);
        o.setVisibility(View.INVISIBLE);
        n.setVisibility(View.INVISIBLE);
        log = findViewById(R.id.loginButton);
        heading = findViewById(R.id.textView);
        login = findViewById(R.id.logintext);
        phone = findViewById(R.id.loginPhone);
        password = findViewById(R.id.passwordLogin);
        ava = findViewById(R.id.available);
        notava = findViewById(R.id.notavailable);
        driverText = findViewById(R.id.nameUser);
        heading.setVisibility(View.INVISIBLE);
        login.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        log.setVisibility(View.VISIBLE);
    }
    public void getdata(View v) {
        dphone = phone.getText().toString();
        dpass = password.getText().toString();
        driverText = findViewById(R.id.nameUser);
        db = FirebaseFirestore.getInstance();
        db.collection("DRIVERS").whereEqualTo("number", dphone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot :task.getResult()) {
                        Log.d("TAG", documentSnapshot.getId() + "=>" + documentSnapshot.getData());
                        ID = documentSnapshot.getId();
                        String enterPass = ((String) documentSnapshot.get("password"));
                        boolean retval = checkEqual(enterPass, dpass);
                        Log.d("RETURN", " "+retval);
                        Log.d("PASSWORDS", " "+ enterPass +dpass);
                        if(retval) {
                            ava.setVisibility(View.VISIBLE);
                            notava.setVisibility(View.VISIBLE);
                            Log.d("PASSWORD", ": "+dpass);
                            String text = " Name : " + ((String) documentSnapshot.get("name")) +
                                    "\n Car Model: " + ((String) documentSnapshot.get("carname")) +
                                    "\n Number : " + ((String) documentSnapshot.get("number")) +
                                    "\n Email: " + ((String) documentSnapshot.get("email"));

                            driverText.setText(text);
                            Toast.makeText(driverlogin.this, "Hello " + ((String) documentSnapshot.get("name")) + "!", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Log.d("ANDPASSWORD" , ":"+dpass);
                            Toast.makeText(driverlogin.this, "INCORRECT PASSWORD!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(driverlogin.this, driverlogin.class);
                            startActivity(intent);
                        }
                    }
                }
            }

        });
        ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("DRIVERS").document(ID).update("available", true);
                Toast.makeText(driverlogin.this, "SET TO AVAILABLE", Toast.LENGTH_SHORT).show();
                int REQUEST_LOCATION = 99;
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setCostAllowed(false);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                provider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(driverlogin.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(driverlogin.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(driverlogin.this, new String[]
                            {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }

                final Location location = locationManager.getLastKnownLocation(provider);

                mylistener = new MyLocationListener();

                if (location != null) {

                    mylistener.onLocationChanged(location);
                } else {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

                locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
                if (location != null) {
                    db.collection("DRIVERS").document(ID).update("latitude", location.getLatitude());
                    db.collection("DRIVERS").document(ID).update("longitude", location.getLongitude());
                }
            }
        });
        notava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("DRIVERS").document(ID).update("available", false);
                Toast.makeText(driverlogin.this,"SET TO UNAVAILABLE",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkEqual(String enterPass, String dpass) {
        boolean ret = true;
        for (int i = 0; i < enterPass.length()-1; i++) {
            if (enterPass.charAt(i) != dpass.charAt(i)) {
                ret = false;
            }
        }
        return ret;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(driverlogin.this, "" + location.getLatitude() + location.getLongitude(),
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(driverlogin.this, provider + "'s status changed to " + status + "!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(driverlogin.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(driverlogin.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }


}