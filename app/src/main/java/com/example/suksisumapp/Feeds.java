package com.example.suksisumapp;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Comparator;

public class Feeds implements Serializable
{

    @Exclude
    private String key;
    private String feed_title;
    private String feed_date;
    private String feed_time;
    private String feed_note;
    private String date;

    public Feeds(){}

    public Feeds(String feed_title, String feed_date, String feed_time, String feed_note, String date)
    {
        this.feed_title = feed_title;
        this.feed_date = feed_date;
        this.feed_time = feed_time;
        this.feed_note = feed_note;
        this.date = date;
    }

    public String getFeedTitle()
    {
        return feed_title;
    }

    public void setFeedTitle(String feed_title)
    {
        this.feed_title = feed_title;
    }

    public String getFeedDate()
    {
        return feed_date;
    }

    public void setFeedDate(String feed_date)
    {
        this.feed_date = feed_date;
    }

    public String getFeedTime()
    {
        return feed_time;
    }

    public void setFeedTime(String feed_time)
    {
        this.feed_time = feed_time;
    }

    public String getFeedNote()
    {
        return feed_note;
    }

    public void setFeedNote(String feed_note)
    {
        this.feed_note = feed_note;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

}
