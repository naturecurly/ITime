package com.itime.team.itime.utils;

/**
 * Created by leveyleonhardt on 12/16/15.
 */

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static String[] weekName = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
            "Sat"};

    public static String[] weekNameStandardTwo = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29;
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    ;

    public static int getCurrentMonthDays() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int[] getPerviousWeekSunday() {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -getWeekDay());
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH) + 1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;
    }

    public static int[] getWeekSunday(int year, int month, int day, int pervious) {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.DAY_OF_MONTH, pervious);
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH) + 1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;

    }

//    public static int getWeekDayFromDate(int year, int month) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(getDateFromString(year, month));
//        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
//        if (week_index < 0) {
//            week_index = 0;
//        }
//        return week_index;
//    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1);
        int firstWeekDay = c.get(Calendar.DAY_OF_WEEK);
        return firstWeekDay;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static int analysePosition(float x, float mCellSpace) {
        int dateX = (int) Math.floor(x / mCellSpace);
        return dateX;
    }

    public static String getDateFromMatrix(int x, int y, int year, int month) {
        return null;
    }

    public static int getDateOfWeek(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    //Return the number of days between two specific days
    public static int diffDate(int oldYear, int oldMonth, int oldDay, int year, int month, int day) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(oldYear, oldMonth - 1, oldDay);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year, month - 1, day);
        long result = Math.abs((cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 60 * 60 * 24));
        return (int) result + 1;
    }

    //Return the millisecond difference between the specific day and current time
    public static long diffDate(String dateStr) {
        if (dateStr.equals("")) {
            return -1;
        }
        Date date = null;
        Date currentDate = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long diff = 0;
        try {
            date = formatter.parse(dateStr);
            diff = currentDate.getTime() - date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    // Return the date after adding by "step"
    public static int[] addDaysBasedOnCalendar(int year, int month, int day, int step) {
        if (step >= 28)
            return null;
        int[] date = new int[3];
        date[0] = year;
        date[1] = month;
        date[2] = day;
        date[2] += step;
        if (date[2] > getMonthDays(year, month)) {
            date[1]++;
            date[2] -= getMonthDays(year, month);
            if (date[1] > 12) {
                date[0]++;
                date[1] = 1;
                return date;
            } else {
                return date;
            }
        } else {
            return date;
        }

    }

    public static Date plusDay(int year, int month, int day, long addDays) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            long days = 24 * 60 * 60 * 1000 * addDays;
            Date changeDate = formatter.parse(year + "-" + month + "-" + day + " 10:00:00");
            date = new Date(changeDate.getTime() + days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date plusDay(int year, int month, int day, int hour, int min, long addDays) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            long days = 24 * 60 * 60 * 1000 * addDays;
            Date changeDate = formatter.parse(year + "-" + month + "-" + day + " " + hour + ":" + min);
            date = new Date(changeDate.getTime() + days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date plusMinute(Date originalTime, int minutes) {
        long period = minutes * 60 * 1000;
        Date returnTime = new Date(originalTime.getTime() + period);
        return returnTime;
    }


    //If the start time is easier than the end time, then the time is seemed feasible
    public static boolean isFeasible(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin) {
        Calendar start = Calendar.getInstance();
        start.set(startYear, startMonth, startDay, startHour, startMin);
        Calendar end = Calendar.getInstance();
        end.set(endYear, endMonth, endDay, endHour, endMin);
        if (start.compareTo(end) >= 0) {
            return false;
        } else {
            return true;
        }
    }

    //Change Time Zone
    private static String dateTransformBetweenTimeZone(Date sourceDate, DateFormat formatter,
                                                       TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
        return formatter.format(new Date(targetTime));
    }

    /*public static Date getLocalTime(String data) {
        Date dateForReturn = null;
        try {
            String[] dataOfJson = data.split(" ");
            String dateOfJson = dataOfJson[0];
            String timeOfJson = dataOfJson[1];
            String timezoneOfJson = dataOfJson[2];
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(dateOfJson + " " + timeOfJson);
            String scrTimeZoneFormat = "GMT" + timezoneOfJson.substring(0, 3) + ":" +
                    timezoneOfJson.substring(3, timezoneOfJson.length());
            TimeZone srcTimeZone = TimeZone.getTimeZone(scrTimeZoneFormat);
            TimeZone destTimeZone = TimeZone.getTimeZone(date.toString().split(" ")[4]);
            dateForReturn = formatter.parse(dateTransformBetweenTimeZone(date, formatter, srcTimeZone, destTimeZone));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateForReturn;
    }*/


    public static String getCurrentTime(String format) {
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String str = dateFormat.format(date);
        return str;
    }

    /**
     * @param data a date in String format "yyyy-MM-dd HH:mm:ss Z"
     * @return a Date Object
     */
    public static Date getLocalDateObject(String data) {
        if (data != null) {
            // formater.getDateInstance can be more efficient
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            formatter.setTimeZone(TimeZone.getDefault());
            try {
                return formatter.parse(data);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date getGMTDate(String data) {
        Date date = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            date = formatter.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Calendar getLocalDateObjectToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Calendar getGMTDateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return cal;
    }

    public static String formatLocalDateObject(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    public static String getDateWithTimeZone(int year, int month, int day, int hour, int min) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, min);
        c.setTimeZone(TimeZone.getDefault());
        return formatter.format(c.getTime());
    }

    public static String getICSTime(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        return formatter.format(calendar.getTime());
    }


    public static String getDateStringFromCalendar(Calendar calendar) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        calendar.setTimeZone(TimeZone.getDefault());
        return formatter.format(calendar.getTime());
    }

    public static String getDateStringFromCalendarGMT(Calendar calendar) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(calendar.getTime());
    }

    public static Calendar getCalendarFromInteger(int day, int month, int year) {
        String dateString = getDateWithTimeZone(year, month, day, 0, 0);
        Date date = getGMTDate(dateString);
        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        return cal;
    }


    public static String formatToReadable(String dateString) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
        Date date = getLocalDateObject(dateString);
        return formatter.format(date);
    }

    public static String formatDate(int day, int month, int year) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return formatter.format(calendar.getTime());
    }
}
