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
            datePeriodDao.insertDatePeriod(new DatePeriod(0, "我的计划", "", DatePeriodUtil.TYPE_TITLE_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(10, "清早", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(20, "早上1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(30, "早上2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(40, "中午", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(50, "下午1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(60, "下午2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(70, "晚上1", "", DatePeriodUtil.TYPE_PERIOD_NAME));
            datePeriodDao.insertDatePeriod(new DatePeriod(80, "晚上2", "", DatePeriodUtil.TYPE_PERIOD_NAME));
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

    // 可能只需要更新一天，也可能需要更新n天
    // 更新后可能将表格全部清除，也可能只是前移
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
                // 该天计划往表格前挪一列
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
                // 添加或不添加至todolist后，清除该天计划
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
//        tableData.put(0, "我的计划");
//        tableData.put(1, "早上1");
//        tableData.put(2, "早上2");
//        tableData.put(3, "中午");
//        tableData.put(4, "下午1");
//        tableData.put(5, "下午2");
//        tableData.put(6, "晚上1");
//        tableData.put(7, "晚上2");
//        tableData.put(8, "1月26");
//        tableData.put(9, "算法Ⅰ");
//        tableData.put(10, "算法Ⅱ");
//        tableData.put(11, "无");
//        tableData.put(12, "app#软件骨架");
//        tableData.put(13, "后端#1");
//        tableData.put(14, "原神#探索稻妻");
//        tableData.put(15, "补作业");
//        tableData.put(16, "1月27");
//        tableData.put(17, "算法Ⅰ");
//        tableData.put(18, "算法Ⅱ");
//        tableData.put(19, "无");
//        tableData.put(20, "app#连接数据库");
//        tableData.put(21, "后端#2");
//        tableData.put(22, "原神#春节活动");
//        tableData.put(23, "剪视频");
//        tableData.put(24, "1月28");
//        tableData.put(25, "算法Ⅰ");
//        tableData.put(26, "算法Ⅱ");
//        tableData.put(27, "无");
//        tableData.put(28, "app#修改数据");
//        tableData.put(29, "后端#2b");
//        tableData.put(30, "原神#锄大地");
//        tableData.put(31, "啊啊啊啊啊啊啊。。。");
//        return tableData;
//    }
}
