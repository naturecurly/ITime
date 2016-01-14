package com.itime.team.itime.bean;

import java.util.Date;

/**
 * Created by mac on 16/1/14.
 */
public class Preference {
    private String preference_type;
    private Date starts_time;
    private Date ends_time;
    private String repeat_type;
    private String userID;

    public void setEnds_time(Date ends_time) {
        this.ends_time = ends_time;
    }

    public void setPreference_type(String preference_type) {
        this.preference_type = preference_type;
    }

    public void setRepeat_type(String repeat_type) {
        this.repeat_type = repeat_type;
    }

    public void setStarts_time(Date starts_time) {
        this.starts_time = starts_time;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getEnds_time() {
        return ends_time;
    }

    public Date getStarts_time() {
        return starts_time;
    }

    public String getPreference_type() {
        return preference_type;
    }

    public String getRepeat_type() {
        return repeat_type;
    }

    public String getUserID() {
        return userID;
    }
}
