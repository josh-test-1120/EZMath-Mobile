package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is the Exam class
 */
public class Exam implements Serializable {
    // Private variables
    private String name, professor, classid;
    private ArrayList<Timestamp> times;

    /**
     * Empty constructor for serialization
     */
    public Exam() {}

    /**
     * Default constructor for the Exam class
     * @param name this is the name of the exam
     * @param professor this is the name of the professor
     * @param times a list of times that an exam can be taken
     */
    public Exam(String name, String professor, ArrayList<Timestamp> times) {
        this.name = name;
        this.professor = professor;
        this.times = times;
    }

    /**
     * Getter for the exam name
     * @return string of the exam name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the exam name
     * @param name string of the exam name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the professor name
     * @return string of the professor name
     */
    public String getProfessor() {
        return professor;
    }

    /**
     * Getter for the professor name
     * @param professor string of the professor name
     */
    public void setProfessor(String professor) {
        this.professor = professor;
    }

    /**
     * Getter for the classID
     * @return string of the classID
     */
    public String getClassID() {
        return classid;
    }

    /**
     * Setter for the classID
     * @param classid string of the classID
     */
    public void setClassID(String classid) {
        this.classid = classid;
    }

    /**
     * Getter for the exam times array
     * @return an ArrayList of Timestamps when exams can be taken
     */
    public ArrayList<Timestamp> getTimes() {
        return times;
    }

    /**
     * Setter for the exam times array
     * @param times an ArrayList of Timestamps when exams can be taken
     */
    public void setTimes(ArrayList<Timestamp> times) {
        this.times = times;
    }
}
