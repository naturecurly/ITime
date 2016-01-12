package com.itime.team.itime.bean;

/**
 * Created by mac on 16/1/12.
 */
public class User {
    private String ID = "cai";
    private String profilePicture = "";

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getID() {
        return ID;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
