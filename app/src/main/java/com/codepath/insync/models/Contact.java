package com.codepath.insync.models;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by Gauri Gadkari on 4/20/17.
 */
@Parcel
public class Contact {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String name;
    public String phoneNumber;
    public String imageUrl;
    public boolean isSelected;

    public Contact(){
        
    }

    public Contact(String name, String phoneNumber, String imageUrl, boolean isSelected){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }
}
