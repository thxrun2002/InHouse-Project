package com.example.quikcar;

import android.graphics.Bitmap;
import android.media.Image;

public class driver {
    private String name;
    private String number;
    private String email;
    private String carname;
    private String password;
    private String carimgref;
    private String faceimgref;
    public double latitude,longitude ;
    public driver() {
    }

    public String getCarimgref() {
        return carimgref;
    }

    public void setCarimgref(String carimgref) {
        this.carimgref = carimgref;
    }

    public String getFaceimgref() {
        return faceimgref;
    }

    public void setFaceimgref(String faceimgref) {
        this.faceimgref = faceimgref;
    }

    public boolean available;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public driver(String name, String number, String email, String carname,
                  String password, String carphotoref, String facephotoref) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.carname = carname;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.password = password;
        this.carimgref = carphotoref;
        this.faceimgref = facephotoref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarname() {
        return carname;
    }

    public void setCarname(String carname) {
        this.carname = carname;
    }
}
