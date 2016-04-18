package com.itime.team.itime.bean;

import com.itime.team.itime.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Weiwei Cai on 16/4/11.
 */
public class MeetingInfo {
    private String latitude;
    private String longitude;
    private String newLatitude;
    private String newLongitude;
    private String location;
    private String newLocation;
    private String name;
    private String newName;
    private String status;
    private String repeat;
    private String newRepeat;
    private boolean punctual;
    private boolean newPunctual;
    private String comment;
    private String newComment;
    private String end;
    private String newEnd;
    private String id;
    private String venue;
    private String hostID;
    private String start;
    private String newStart;
    private String newVenue;
    private String token;

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public Calendar getEnd() {
        Date date = DateUtil.getLocalDateObject(end);
        return DateUtil.getLocalDateObjectToCalendar(date);
    }

    public String getHostID() {
        return hostID;
    }

    public String getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLocation() {
        return location;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNewComment() {
        return newComment;
    }

    public Calendar getNewEnd() {
        Date date = DateUtil.getLocalDateObject(newEnd);
        return DateUtil.getLocalDateObjectToCalendar(date);
    }

    public String getNewLatitude() {
        return newLatitude;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public String getNewLongitude() {
        return newLongitude;
    }

    public String getNewName() {
        return newName;
    }

    public boolean getNewPunctual() {
        return newPunctual;
    }

    public String getNewRepeat() {
        return newRepeat;
    }

    public Calendar getNewStart() {
        Date date = DateUtil.getLocalDateObject(newStart);
        return DateUtil.getLocalDateObjectToCalendar(date);
    }

    public String getNewVenue() {
        return newVenue;
    }

    public boolean getPunctual() {
        return punctual;
    }

    public String getRepeat() {
        return repeat;
    }

    public Calendar getStart() {
        Date date = DateUtil.getLocalDateObject(start);
        return DateUtil.getLocalDateObjectToCalendar(date);
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getVenue() {
        return venue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public void setNewEnd(String newEnd) {
        this.newEnd = newEnd;
    }

    public void setNewLatitude(String newLatitude) {
        this.newLatitude = newLatitude;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    public void setNewLongitude(String newLongitude) {
        this.newLongitude = newLongitude;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public void setNewPunctual(boolean newPunctual) {
        this.newPunctual = newPunctual;
    }

    public void setNewRepeat(String newRepeat) {
        this.newRepeat = newRepeat;
    }

    public void setNewStart(String newStart) {
        this.newStart = newStart;
    }

    public void setNewVenue(String newVenue) {
        this.newVenue = newVenue;
    }

    public void setPunctual(boolean punctual) {
        this.punctual = punctual;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
