package com.itime.team.itime.bean;

import java.util.Date;

/**
 * Created by mac on 16/1/14.
 */
public class Preference {
    private String preferenceId;
    private boolean deleted;
    private boolean useRepeat;
    private Date settingDate;
    private String preference_type;
    private Date starts_time;
    private Date ends_time;
    private String repeat_type;
    private String userID;

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMin;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMin;

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartYear() {
        return startYear;
    }


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

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isUseRepeat() {
        return useRepeat;
    }

    public void setUseRepeat(boolean useRepeat) {
        this.useRepeat = useRepeat;
    }

    public Date getSettingDate() {
        return settingDate;
    }

    public void setSettingDate(Date settingDate) {
        this.settingDate = settingDate;
    }
}
