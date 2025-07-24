package com.example.sunmail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Mail implements Serializable {
    private String id;
    @SerializedName("from")
    private String sender;
    @SerializedName("to")
    private String receiver;
    private String subject;
    @SerializedName("body")
    private String snippet;
    private String time;
    private boolean starred;
    private boolean read;
    private Date createdAt;

    public Mail(String sender, String receiver, String subject, String snippet, String time, boolean read, boolean starred
            , Date createdAt, String id) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.snippet = snippet;
        this.time = time;
        this.read = read;
        this.starred = starred;
        this.createdAt = createdAt;
        this.id = id;
    }
    public String getId(){
        return id;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiver() {
        return receiver;
    }

    // getters
    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getSnippet() {
        int MAX_SNIPPET_LENGTH = 50;
        if (snippet.length() <= MAX_SNIPPET_LENGTH) {
            return snippet;
        } else {
            return snippet.substring(0, MAX_SNIPPET_LENGTH) + "...";
        }
    }
public String getBody(){
        return snippet;
}

    public String getTime() {
        return time;
    }

    public boolean isStarred() {
        return starred;
    }

    public boolean isRead() {
        return read;
    }
}
