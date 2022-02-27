package com.airy.framingnext.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DatePeriod.class, Todo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatePeriodDao datePeriodDao();
    public abstract TodoDao todoDao();
    private static volatile AppDatabase INSTANCE;

    static AppDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
