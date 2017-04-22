package com.codepath.insync.models;

/**
 * Created by Gauri Gadkari on 4/20/17.
 */

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

    String name;
    String phoneNumber;
    String imageUrl;
    boolean isSelected;

    public Contact(String name, String phoneNumber, String imageUrl, boolean isSelected){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }
}
