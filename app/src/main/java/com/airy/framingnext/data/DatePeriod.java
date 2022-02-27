package com.airy.framingnext.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DatePeriod {
    @PrimaryKey
    public int cid;
    public String content;
    public String detail;
    public int type;

    public DatePeriod(int cid, String content, String detail, int type) {
        this.cid = cid;
        this.content = content;
        this.detail = detail;
        this.type = type;
    }
}
