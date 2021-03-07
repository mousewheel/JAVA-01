package com.mousewheel.mall.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String formatDate(Date dateTime, String formatPartten){
        if(null == dateTime){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatPartten);
        return sdf.format(dateTime);
    }


    public static String formatDate(Date dateTime){
        String formatPartten = "yyyy-MM-dd HH:mm:ss";
          return formatDate(dateTime, formatPartten);
    }
}
