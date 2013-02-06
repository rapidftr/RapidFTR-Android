package com.rapidftr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RapidFtrDateTime {

    private static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String formatForChildRegister = "dd MMM yyyy";

    private Date dateTime;

    private RapidFtrDateTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        this.dateTime = calendar.getTime();
    }

    public RapidFtrDateTime(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(year, month-1, day, 0, 0, 0);
        this.dateTime = calendar.getTime();
    }

    public static RapidFtrDateTime now() {
        return new RapidFtrDateTime();
    }

    public String defaultFormat(){
        SimpleDateFormat simpleDateFormat = getDefaultSimpleDateFormat();
        return simpleDateFormat.format(dateTime);
    }

    public static Calendar getDateTime(String dateTime) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = getDefaultSimpleDateFormat();
        calendar.setTime(simpleDateFormat.parse(dateTime));
        return calendar;
    }

    private static SimpleDateFormat getDefaultSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    @Override
    public String toString() {
        return defaultFormat();
    }
}
