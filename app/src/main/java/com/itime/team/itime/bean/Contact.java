package com.itime.team.itime.bean;

import java.util.ArrayList;

/**
 * Created by Weiwei Cai on 16/2/6.
 */
public class Contact {
    private String name;
    private ArrayList<String> photoNumber;
    private String eMail;

    public String geteMail() {
        return eMail;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPhotoNumber() {
        return photoNumber;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoNumber(ArrayList<String> photoNumber) {
        this.photoNumber = photoNumber;
    }
}

