package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Scheduled implements Comparable {
    public String name, examid, userid;
    public Timestamp date;

    public Scheduled() {}

    public Scheduled(String name, String examid, String userid, Timestamp date) {
        this.name = name;
        this.examid = examid;
        this.userid = userid;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getExamid() {
        return examid;
    }

    public void setExamid(String examid) {
        this.examid = examid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * CompareTo override to implement comparable for sorting
     * @param o this is the other object to compare to
     * @return integer representing the comparison
     */
    @Override
    public int compareTo(Object o) {
        Date currentDate = this.getDate().toDate();
        Scheduled other = (Scheduled) o;
        Date otherDate = other.getDate().toDate();
        if (currentDate.equals(otherDate)) return 0;
        else if (currentDate.before(otherDate)) return 1;
        else return -1;
    }
}
