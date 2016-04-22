package com.itime.team.itime.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 5/04/16.
 */
public class Invitation {
    String orgnizer;
    String org_email;

    public Map<String, String> invitees = new HashMap<String,String>();

    public Invitation(String orgnizer, String org_email){
        this.orgnizer = orgnizer;
        this.org_email = org_email;
    }

    public void addInvitees(String name, String email){
        invitees.put(email,name);
    }

    public void deleteInvitees(String email){
        invitees.remove(email);
    }

    public String makeAttendeeBody(String name, String email, String status, String role){

        return "ATTENDEE;CN=" + name + ";ROLE=" + role + ";PARTSTAT=" + status + ";RSVP=TRUE:mailto:" + email;
    }

    public String makeOrgnizerAttBody(){
        return "ATTENDEE;CN=" + this.orgnizer + ";ROLE=CHAIR;PARTSTAT=ACCEPTED;RSVP=TRUE:mailto:" + this.org_email;
    }

    public String makeOrgnizerBody(){

        return "ORGANIZER;" + "CN=" + this.orgnizer + ":" + "mailto:" + this.org_email;
    }

    public String makeRequiredInfoBody(int sequence){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        Date date = new Date();
        String formatedDate = formatter.format(date);

        return "METHOD:REQUEST" + "\n"
                + "CREATED:" + formatedDate + "\n"
                + "LAST-MODIFIED:" + formatedDate + "\n"
                + "SEQUENCE:" + sequence + "\n"
                + "STATUS:CONFIRMED" + "\n";
    }

}
