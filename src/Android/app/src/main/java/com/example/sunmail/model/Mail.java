package com.example.sunmail.model;

public class Mail {
    private String sender;
    private String subject;
    private String snippet;
    private String time;

    public Mail(String sender, String subject, String snippet, String time) {
        this.sender  = sender;
        this.subject = subject;
        this.snippet = snippet;
        this.time    = time;
    }

    // getters
    public String getSender()  { return sender; }
    public String getSubject() { return subject; }
    public String getSnippet() { return snippet; }
    public String getTime()    { return time; }
}
