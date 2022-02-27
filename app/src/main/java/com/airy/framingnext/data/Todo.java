package com.airy.framingnext.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Todo {
    @PrimaryKey(autoGenerate = true)
    int id;
    String content;

    public Todo(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
