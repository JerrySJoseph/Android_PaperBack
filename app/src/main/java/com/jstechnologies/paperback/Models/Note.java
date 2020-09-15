package com.jstechnologies.paperback.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "title")
    String title;
    @ColumnInfo(name = "subtitle")
    String subtitle;
    @ColumnInfo(name="timestamp")
    long timestamp;
    @ColumnInfo(name = "notetext")
    String notetext;
    @ColumnInfo(name = "imagepath")
    String imagepath;
    @ColumnInfo(name = "color")
    String color;
    @ColumnInfo(name = "weblink")
    String weblink;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNotetext() {
        return notetext;
    }

    public void setNotetext(String notetext) {
        this.notetext = notetext;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWeblink() {
        return weblink;
    }

    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }

    @NonNull
    @Override
    public String toString() {
        return title+" : "+new DateTime(timestamp).toString(DateTimeFormat.shortDateTime());
    }
}
