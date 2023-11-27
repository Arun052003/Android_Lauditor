package com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels;

public class MeetingModel {
    int count;
    String date;
    String from_ts;
    String to_ts;

    String time;
    String subject;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom_ts() {
        return from_ts;
    }

    public void setFrom_ts(String from_ts) {
        this.from_ts = from_ts;
    }

    public String getTo_ts() {
        return to_ts;
    }

    public void setTo_ts(String to_ts) {
        this.to_ts = to_ts;
    }

    public MeetingModel(String date, String from_ts, String to_ts, String subject) {
        this.date = date;
        this.from_ts = from_ts;
        this.to_ts = to_ts;
        this.subject = subject;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
