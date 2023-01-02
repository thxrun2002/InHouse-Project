package com.example.quikcar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }
    public void userclick(View v){
        Intent i =  new Intent(this, useractivity.class);
        startActivity(i);
    }
    public void driverclick(View v){
        Intent i =  new Intent(this, driverlogin.class);
        startActivity(i);
    }

}