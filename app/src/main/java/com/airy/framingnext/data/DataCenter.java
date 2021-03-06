package com.airy.framingnext.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.airy.framingnext.utils.DatePeriodUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataCenter {
    private static DataCenter instance;
    private final static String DATA_SHARED_PREFERENCE_TAG = "DATA_SHARED_PREFERENCE";
    private final static String DATE_PERIOD_DAY_COUNT_TAG = "DATE_PERIOD_DAY_COUNT";
    private final static String DATE_PERIOD_PERIOD_COUNT_TAG = "DATE_PERIOD_ITEM_COUNT";
    private final static String IS_DATE_PERIOD_FIRST_TIME_TAG = "IS_FIRST_TIME";
    private static final int DEFAULT_DATE_PERIOD_DAY = 2;
    private static final int DEFAULT_DATE_PERIOD_PERIODS = 8;
    private final DatePeriodDao datePeriodDao;
    private final TodoDao todoDao;
    private Context context;
    private final SharedPreferences preferences;

    public int getDatePeriodPeriodCount() {
        return datePeriodPeriodCount;
    }

    public int getDatePeriodDayCount() {
        return datePeriodDayCount;
    }

    private int datePeriodPeriodCount;
    private int datePeriodDayCount;
    private static final String TAG = "AppDateCenter";

    public static DataCenter getInstance(Context context) {
        if (instance == null) {
            instance = new DataCenter(context);
        }
        return instance;
    }

    private DataCenter(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        datePeriodDao = db.datePeriodDao();
        todoDao = db.todoDao();
        preferences = context.getSharedPreferences(DATA_SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        tableInit();
    }

    private void tableInit() {
        if (isDatePeriodFirstTime()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(DATE_PERIOD_DAY_COUNT_TAG, DEFAULT_DATE_PERIOD_DAY);
            editor.putInt(DATE_PERIOD_PERIOD_COUNT_TAG, DEFAULT_DATE_PERIOD_PERIODS);
            editor.putBoolean(IS_DATE_PERIOD_FIRST_TIME_TAG, false);
            editor.apply();
            datePeriodDao.insertDatePeriod(new DatePeriod(0, "????????????", "", DatePeriodUtil.TYPE_TITLE_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(10, "??????", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(20, "??????1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(30, "??????2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(40, "??????", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(50, "??????1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(60, "??????2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(70, "??????1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(80, "??????2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            for (int i = 1; i <= DEFAULT_DATE_PERIOD_DAY; i++) {
                datePeriodDao.insertDatePeriod(DatePeriodUtil.getDayTypeDatePeriod(i));
                for (int j = 1; j <= DEFAULT_DATE_PERIOD_PERIODS; j++) {
                    datePeriodDao.insertDatePeriod(new DatePeriod(j * 10 + i, "", "", DatePeriodUtil.TYPE_REGULAR_ITEM));
                }
            }
        }
        datePeriodPeriodCount = preferences.getInt(DATE_PERIOD_PERIOD_COUNT_TAG, DEFAULT_DATE_PERIOD_PERIODS);
        datePeriodDayCount = preferences.getInt(DATE_PERIOD_DAY_COUNT_TAG, DEFAULT_DATE_PERIOD_DAY);
    }

    public boolean isDatePassed() {
        return isDatePassed(1);
    }

    public boolean isDatePassed(int cid) {
        DatePeriod curr = datePeriodDao.getDatePeriodByCID(cid);
        return Integer.parseInt(curr.content) < DatePeriodUtil.getTodayNum();
    }

    public boolean isDatePeriodFirstTime() {
        return preferences.getBoolean(IS_DATE_PERIOD_FIRST_TIME_TAG, true);
    }

    public ArrayList<DatePeriod> getDatePeriodList() {
        return (ArrayList<DatePeriod>) datePeriodDao.getDatePeriodList();
    }

    public void updateDatePeriod(DatePeriod curr) {
        datePeriodDao.updateDatePeriod(curr);
    }

    // ???????????????????????????????????????????????????n???
    // ????????????????????????????????????????????????????????????
    public void updateTable(boolean addToTodoList) {
        boolean pushForward = false;
        int forward = 0;
        for (int i = 1; i <= datePeriodDayCount; i++) {
//            pushForward = pushForward || (i != 1 && !isDatePassed(i));
            if(!pushForward){
                if(i!=1 && !isDatePassed(i)){
                    pushForward = true;
                    forward = i-1;
                }
            }
            if(pushForward){
                // ?????????????????????????????????
                for (int j = 0; j <= datePeriodPeriodCount; j++) {
                    int cid = j*10+i;
                    int newCid = j*10+i-forward;
                    DatePeriod curr = datePeriodDao.getDatePeriodByCID(cid);
                    DatePeriod next = datePeriodDao.getDatePeriodByCID(newCid);
                    next.content = curr.content;
                    if(i==datePeriodDayCount && j==0){
                        curr.content = Integer.toString(DatePeriodUtil.getTodayNum(cid - 1));
                    }else{
                        curr.content = "";
                    }
                    datePeriodDao.updateDatePeriod(next);
                    datePeriodDao.updateDatePeriod(curr);
                }
            }else{
                // ?????????????????????todolist????????????????????????
                datePeriodDao.updateDatePeriod(DatePeriodUtil.getDayTypeDatePeriod(i));
                for (int j = 1; j <= datePeriodPeriodCount; j++) {
                    int cid = j*10+i;
                    DatePeriod curr = datePeriodDao.getDatePeriodByCID(cid);
                    if(addToTodoList){
                        String content = curr.content;
                        if (!content.trim().equals("")) todoDao.insertTodo(new Todo(content));
                    }
                    curr.content = "";
                    datePeriodDao.updateDatePeriod(curr);
                }
            }
        }
    }

    public List<Todo> getTodoList(){
        return todoDao.getTodoList();
    }


//    public void deleteDateViaDate(int date){
//        dateDao.deleteDateViaTitle(DateUtil.getDateToString(date));
//    }
//
//    public Map<Integer, String> getFakeData(){
//        Map<Integer, String> tableData = new HashMap<>();
//        // default: TableDateCount = 3, TableDailyItemCount = 7
//        tableData.put(0, "????????????");
//        tableData.put(1, "??????1");
//        tableData.put(2, "??????2");
//        tableData.put(3, "??????");
//        tableData.put(4, "??????1");
//        tableData.put(5, "??????2");
//        tableData.put(6, "??????1");
//        tableData.put(7, "??????2");
//        tableData.put(8, "1???26");
//        tableData.put(9, "?????????");
//        tableData.put(10, "?????????");
//        tableData.put(11, "???");
//        tableData.put(12, "app#????????????");
//        tableData.put(13, "??????#1");
//        tableData.put(14, "??????#????????????");
//        tableData.put(15, "?????????");
//        tableData.put(16, "1???27");
//        tableData.put(17, "?????????");
//        tableData.put(18, "?????????");
//        tableData.put(19, "???");
//        tableData.put(20, "app#???????????????");
//        tableData.put(21, "??????#2");
//        tableData.put(22, "??????#????????????");
//        tableData.put(23, "?????????");
//        tableData.put(24, "1???28");
//        tableData.put(25, "?????????");
//        tableData.put(26, "?????????");
//        tableData.put(27, "???");
//        tableData.put(28, "app#????????????");
//        tableData.put(29, "??????#2b");
//        tableData.put(30, "??????#?????????");
//        tableData.put(31, "??????????????????????????????");
//        return tableData;
//    }
}
