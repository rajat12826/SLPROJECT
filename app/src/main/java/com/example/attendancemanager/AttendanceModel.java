package com.example.attendancemanager;

public class AttendanceModel {
    private int id;
    private String subject;
    private int attendedClasses;
    private int totalClasses;

    public AttendanceModel(int id, String subject, int attendedClasses, int totalClasses) {
        this.id = id;
        this.subject = subject;
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }
}