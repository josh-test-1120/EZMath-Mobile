package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String name, professor, classid;
    private ArrayList<Timestamp> times;

    public Exam() {}

    public Exam(String name, String professor) {
        this.name = name;
        this.professor = professor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getClassID() {
        return classid;
    }

    public void setClassID(String classid) {
        this.classid = classid;
    }

    public ArrayList<Timestamp> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<Timestamp> times) {
        this.times = times;
    }
}
