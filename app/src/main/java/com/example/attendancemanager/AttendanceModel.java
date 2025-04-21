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

    public String getSubject() {
        return subject;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

}