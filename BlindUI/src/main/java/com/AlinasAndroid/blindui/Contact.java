package com.AlinasAndroid.blindui;

/**
 * Created by Alina on 6/17/13.
 */
public class Contact {
    private String mName;
    private String mPhoneNumber;
    private long mID;
    private String mPhoto;

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPhoneNumberDigits() {
        String phoneDigits = "";
        int i = 0;
        while ( i < mPhoneNumber.length()) {
            phoneDigits += " " + mPhoneNumber.charAt(i);
            i++;
        };
        return phoneDigits;
    }


    public void setID(long ID) {
        mID = ID;
    }

    public long getID() {
        return mID;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getPhoto() {
        return mPhoto;
    }
}
