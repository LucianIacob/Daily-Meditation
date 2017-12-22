package com.dailymeditation.android.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created with <3 by liacob & <Pi> on 29-Sep-17.
 */

@Entity(tableName = "passage")
public class Passage {

    @ColumnInfo(name = "verse")
    private final String verse;
    @ColumnInfo(name = "path")
    private final String path;
    @ColumnInfo(name = "date")
    private final String date;
    @PrimaryKey(autoGenerate = true)
    private int uid;

    public Passage(String verse, String path, String date) {
        this.verse = verse;
        this.path = path;
        this.date = date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getVerse() {
        return verse;
    }

    public String getPath() {
        return path;
    }

    public String getDate() {
        return date;
    }
}
