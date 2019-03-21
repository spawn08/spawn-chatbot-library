package com.spawn.ai.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public DateTimeUtils() {
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String date = simpleDateFormat.format(new Date().getTime());
        return date;
    }
}
