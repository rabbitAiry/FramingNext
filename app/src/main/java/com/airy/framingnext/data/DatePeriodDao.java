package com.airy.framingnext.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DatePeriodDao {
    @Query("SELECT * FROM DatePeriod ORDER BY cid")
    List<DatePeriod> getDatePeriodList();

    @Query("SELECT * FROM DatePeriod WHERE cid = :cid")
    DatePeriod getDatePeriodByCID(int cid);

    @Insert
    long insertDatePeriod(DatePeriod datePeriod);

    @Update
    int updateDatePeriod(DatePeriod datePeriod);

    @Delete
    void deleteDatePeriod(DatePeriod datePeriod);
}
