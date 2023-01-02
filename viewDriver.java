package com.example.quikcar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class viewDriver extends AppCompatActivity {

    RecyclerView recyclerView;
    double userLatitude,userLongitude,maxlat,minlat,maxlong,minlong;
    ArrayList<driver> driverArrayList;
    adapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver);
        userLatitude = this.getIntent().getDoubleExtra("latitude", userLatitude);
        maxlat = userLatitude + 0.025;
        minlat = userLatitude - 0.025;
        userLongitude = this.getIntent().getDoubleExtra("longitude", userLongitude);
        maxlong = userLongitude + 0.025;
        minlong = userLongitude - 0.025;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("FINDING NEARBY DRIVERS....");
        progressDialog.show();
        recyclerView = findViewById(R.id.driverList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        driverArrayList = new ArrayList<driver>();
        myAdapter = new adapter(viewDriver.this,driverArrayList);
        recyclerView.setAdapter(myAdapter);
        getdata();
    }
    private void getdata(){
         db.collection("DRIVERS")
                 .whereEqualTo("available", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Log.e("ERROR", error.getMessage());
                    return;
                }
                for(DocumentChange dc: value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        driver d = dc.getDocument().toObject(driver.class);
                        if(d.longitude>minlong && d.longitude<maxlong && d.latitude >minlat && d.latitude < maxlat ) {
                            driverArrayList.add(d);
                        }

                    }
                    myAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            }
        });
    }
}