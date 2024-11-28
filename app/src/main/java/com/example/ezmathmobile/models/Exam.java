package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Exam {
    private String name, professor, classID;
    private List<Timestamp> times;

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
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public List<Timestamp> getTimes() {
        return times;
    }

    public void setTimes(List<Timestamp> times) {
        this.times = times;
    }
}
