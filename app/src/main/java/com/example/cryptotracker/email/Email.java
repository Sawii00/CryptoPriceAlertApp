package com.example.cryptotracker.email;

public class Email {

    public String target;
    public String subject;
    public String text;
    public String filePath;

    public Email(String target, String subject, String text, String file)
    {
        this.target = target;
        this.subject = subject;
        this.text = text;
        this.filePath = file;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
