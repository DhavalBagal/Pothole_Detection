package com.android.citygroom;

public class NewsArticle
{
    String newsid, headline,newscontent, timestamp;


    public NewsArticle() {
    }

    public NewsArticle(String newsid, String headline, String newscontent, String timestamp) {
        this.headline = headline;
        this.newscontent = newscontent;
        this.timestamp = timestamp;
    }

    public String getNewsid() {
        return newsid;
    }

    public String getHeadline() {
        return headline;
    }

    public String getNewscontent() {
        return newscontent;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
