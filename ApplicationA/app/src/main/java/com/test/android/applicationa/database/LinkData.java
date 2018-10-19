package com.test.android.applicationa.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "link")
public class LinkData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String link;
    private int status;
    @ColumnInfo(name = "open_time")
    private Date openTime;

    @Ignore
    public LinkData(){
    }

    @Ignore
    public LinkData(String link, int status, Date openTime){
        this.link = link;
        this.status = status;
        this.openTime = openTime;
    }

    public LinkData(int id, String link, int status, Date openTime){
        this.id = id;
        this.link = link;
        this.status = status;
        this.openTime = openTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }
}
