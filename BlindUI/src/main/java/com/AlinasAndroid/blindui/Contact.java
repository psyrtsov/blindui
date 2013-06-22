package com.AlinasAndroid.blindui;

/**
 * Created by Alina on 6/17/13.
 */
public class Contact {
    private String mName;
    private String mPhoneNumber;
    private long mID;
    private String mPhoto;
    private String status;
    private int numTypeInt;
    private String numTypeString;

    public String getNumTypeString() {
        return numTypeString;
    }

    public void setNumTypeString(String numTypeString) {
        this.numTypeString = numTypeString;
    }

    public int getNumTypeInt() {
        return numTypeInt;
    }

    public void setNumTypeInt(int numTypeInt) {
        this.numTypeInt = numTypeInt;
        switch (numTypeInt) {
            case 19:  numTypeString = "Assistant";
                break;
            case 8:  numTypeString = "Callback";
                break;
            case 9:  numTypeString = "Car";
                break;
            case 10:  numTypeString = "Company Main";
                break;
            case 5:  numTypeString = "Fax Home";
                break;
            case 4:  numTypeString = "Fax Work";
                break;
            case 1:  numTypeString = "Home";
                break;
            case 11:  numTypeString = "ISDN";
                break;
            case 12:  numTypeString = "Main";
                break;
            case 20: numTypeString = "MMS";
                break;
            case 2: numTypeString = "Mobile";
                break;
            case 7: numTypeString = "Other";
                break;
            case 13:  numTypeString = "Other Fax";
                break;
            case 6:  numTypeString = "Pager";
                break;
            case 14:  numTypeString = "Radio";
                break;
            case 15:  numTypeString = "Telex";
                break;
            case 16:  numTypeString = "TTY TDD";
                break;
            case 3: numTypeString = "Work";
                break;
            case 17: numTypeString = "Work Mobile";
                break;
            case 18: numTypeString = "Work Pager";
                break;
            default: numTypeString = "";
                break;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
