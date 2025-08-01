package com.example.sunmail.model;

public class ComposeForm {
    private String to;
    private String subject;
    private String body;
    private String draftId;

    public ComposeForm() {}

    public ComposeForm(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public boolean isValid() {
        return to != null && !to.trim().isEmpty() &&
               subject != null && !subject.trim().isEmpty();
    }

    public boolean hasContent() {
        return (to != null && !to.trim().isEmpty()) ||
               (subject != null && !subject.trim().isEmpty()) ||
               (body != null && !body.trim().isEmpty());
    }
}
