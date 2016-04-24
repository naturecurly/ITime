package com.itime.team.itime.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 5/04/16.
 */

public class ICS {
    private String head = "BEGIN:VCALENDAR\nVERSION:2.0\nBEGIN:VEVENT";
    private String tail = "END:VEVENT\nEND:VCALENDAR";
    private Context mContext;

    //The value in 'validation' means standard attributes in 'icalendar'(RFC 5545),
    //but I just input the attributes we need now.
    private String[] validation = {"UID","SUMMARY","DESCRIPTION","DTSTART","DTEND","DTSTAMP","RRULE","LOCATION","ATTENDEE","ORGANIZER"};

    Invitation invitation = new Invitation("","");

    private Map<String, String> dict = new HashMap<String,String>();

    private boolean hasInvitation = false;

    //No args constructor, is used for load a ics file and access the information in this file.

//    public ICS(){
//
//    }

    //constructor with args, the required attrs are key attr for a event. Thus is forced.
    //Note:
    //Any date should be 'UTC' time and be formated to'YearMonthDayTHourMiniteSecond
    //' e.g: 20151225T182030, which means
    //Year:2015 Month:12 Day:25 Hour:18 Minite:20 Second:30.

    public ICS(String event_id, String event_name, String event_description, String event_start, String event_end, Context mContext)
    {
        this.mContext = mContext;
        dict.put("UID",event_id);
        dict.put("SUMMARY",event_name);
        dict.put("DESCRIPTION",event_description);
        dict.put("DTSTART",event_start);
        dict.put("DTEND",event_end);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        Date date = new Date();
        String formatedDate = formatter.format(date);

        dict.put("DTSTAMP", formatedDate);
    }

    //Note: If we want to add the Repeated Event, the form should be
    // attrName:RRULE, attrContent:MONTHLY/WEEKLY/DAYLY
    public void addAttr(String attrName, String attrContent)
    {
        dict.put(attrName, attrContent);
    }

    public void removeAttr(String attrName){
        dict.remove(attrName);
    }

    //Calling this function after creating a completed "Invitation".
    public void attachInvitation(Invitation invitation){
        this.invitation = invitation;
        this.hasInvitation = true;
    }

    public File getTempFile(Context context, String fileName) {
        File file = null;
//        String fileName = Uri.parse(url).getLastPathSegment();
        try {
            file = File.createTempFile(fileName, null, mContext.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
    //Note: Any attributes beyond 'Standard Attributes' will be add prefix 'X-', which
    //means this attributes is customized and other app would not access it.
    public File createICS(String fileName){
        String mainBody = this.configureICSMainBody();
        String invitationBody = this.configureInvitationBody();

        String result = head + "\n" + mainBody + invitationBody + tail;
        FileOutputStream outputStream;


        try{
            File file = getTempFile(mContext, fileName);

            InputStream in = null;
            try {
                System.out.println("以字节为单位读取文件内容，一次读一个字节：");
                // 一次读一个字节
                in = new FileInputStream(file);
                int tempbyte;
                while ((tempbyte = in.read()) != -1) {
                    System.out.write(tempbyte);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(result.getBytes());

            outputStream.close();
            return file;
//            Writer writer = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream(filePath), "utf-8"));
//            try{
//                writer.write(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    //How to use? Same as 'createICS', but the 'filePath' should be the full path of old ics file. Then you will get a new ICS file with same path(name) and new information of event.
    //Note: This func is to update the existing ICS file instead of creating a new one, so you have to guarantee the eventID should be same as the one in old ics file.
    //The 'filePath' is the full path of existing ICS file. e.g. David/folder/test.ics
    //Then the new ics file will be created with new information and replace the old ics file.
    public void updateICS(String filePath){

        File file = new File(filePath);

        if (file.exists() && !file.isDirectory()) {
            // file exist

            try{
                BufferedReader br = new BufferedReader(new FileReader(filePath));

                try{
                    String line = br.readLine();

                    while (line != null) {
                        if (line.startsWith("SEQUENCE")) {
                            String[] components = line.split(":");
                            int old_sequence = Integer.parseInt(components[1]);
                            this.reCreateICS(filePath, old_sequence + 1);
                            break;
                        }

                        line = br.readLine();

                        if (line == null) {
                            System.out.print("Error...ICS file has no attr named 'sequence'");
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String configureICSMainBody(){
        String body = "";

        if(!dict.isEmpty()){

            for(Map.Entry<String, String> entry:dict.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                if (Arrays.asList(validation).contains(key)) {

                    switch (key) {
                        case "RRULE":
                            body += key + ":FREQ=" + value + "\n";
                            break;

                        case "ATTENDEE":
                            body += key + ";";
                            break;
                        default:
                            body += key + ":" + value + "\n";
                            break;
                    }

                } else {
                    body += "X-" + key + ":" + value + "\n";
                }
            }

            for(Map.Entry<String, String> entry:dict.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();

                if (Arrays.asList(validation).contains(key))  {

                    switch (key){
                        case "RRULE":
                            body += key + ":FREQ=" + value + "\n";
                            break;

                        case "ATTENDEE":
                            body += key + ";";
                            break;
                        default:
                            body += key + ":" + value + "\n";
                            break;
                    }

                }else{
                    body += "X-" + key + ":" + value + "\n";
                }
            }
        }

        return body;
    }

    private String configureInvitationBody(){
        String invitationBody = "";

        if(hasInvitation){
            String attendeeBody = "";

            for(Map.Entry<String, String> entry:this.invitation.invitees.entrySet()){
                String email = entry.getKey();
                String name = entry.getValue();

                attendeeBody += invitation.makeAttendeeBody(name, email, "NEED-ACTION", "REQ-PARTICIPANT") + "\n";
            }


            invitationBody = invitation.makeOrgnizerAttBody() + "\n" + attendeeBody + invitation.makeOrgnizerBody() + "\n" + invitation.makeRequiredInfoBody(0);
        }

        return invitationBody;
    }

    private String updateInvitationBody(int newSq){
        String invitationBody = "";

        if (hasInvitation){
            String attendeeBody = "";

            for(Map.Entry<String, String> entry:this.invitation.invitees.entrySet()){
                String email = entry.getKey();
                String name = entry.getValue();

                attendeeBody += invitation.makeAttendeeBody(name, email,"NEED-ACTION", "REQ-PARTICIPANT") + "\n";
            }


            invitationBody = invitation.makeOrgnizerAttBody() + "\n" + attendeeBody + invitation.makeOrgnizerBody() + "\n" + invitation.makeRequiredInfoBody(newSq);
        }

        return invitationBody;
    }

    private void reCreateICS(String filePath, int newSq) {
        String mainBody = this.configureICSMainBody();
        String invitationBody = this.updateInvitationBody(newSq);
        String result = head + "\n" + mainBody + invitationBody + tail;

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), "utf-8"));

            try {
                writer.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
