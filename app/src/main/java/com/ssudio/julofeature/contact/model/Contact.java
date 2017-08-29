package com.ssudio.julofeature.contact.model;

import android.graphics.Bitmap;

public class Contact {
    private Bitmap image;
    private String name;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getImagePath() {
        return image;
    }

    public void setImagePath(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
