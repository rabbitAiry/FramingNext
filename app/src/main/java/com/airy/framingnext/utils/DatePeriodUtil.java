package com.airy.framingnext.utils;


import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import com.airy.framingnext.data.DatePeriod;

import java.util.Calendar;

public class DatePeriodUtil {
    public static final int TYPE_TITLE_NAME = 0;
    public static final int TYPE_PERIOD_NAME = 1;
    public static final int TYPE_WORK_DAY = 2;
    public static final int TYPE_REST_DAY = 3;
    public static final int TYPE_REGULAR_ITEM = 4;
    public static final int TYPE_ABANDON_ITEM = 5;

    /**
     * @param date e.g: 20220128 = 2022/01/28
     * @return
     */
    public static String getDateToString(int date) {
        Calendar c = Calendar.getInstance();
        c.set(date / 10000, date % 10000 / 100 - 1, date % 100);
        return getDateToString(c);
    }

    public static String getDateToString(Calendar c) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd");
        return format.format(c.getTime());
    }

    public static int getDateToInt(Calendar c){
        return c.get(Calendar.YEAR)*10000+(c.get(Calendar.MONTH)+1)*100+c.get(Calendar.DATE);
    }

    public static String getTodayText() {
        Calendar c = Calendar.getInstance();
        return getDateToString(c);
    }

    public static String getTodayText(int add) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, add);
        return getDateToString(c);
    }

    public static int getTodayNum() {
        Calendar c = Calendar.getInstance();
        return getDateToInt(c);
    }

    public static int getTodayNum(int add) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, add);
        return getDateToInt(c);
    }

    public static boolean isContentValid(String text, Context context) {
        // TODO: 不允许有';'， 发送toast告知用户
        return true;
    }

    public static DatePeriod getDayTypeDatePeriod(int cid){
        return new DatePeriod(cid, Integer.toString(getTodayNum(cid - 1)), null, DatePeriodUtil.TYPE_WORK_DAY);
    }
}
