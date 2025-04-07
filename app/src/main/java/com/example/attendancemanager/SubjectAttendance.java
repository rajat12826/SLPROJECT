package com.example.attendancemanager;


public class SubjectAttendance {
    int subjectId;
    String subjectName;
    int attended;
    int total;

    public SubjectAttendance(int id, String name, int attended, int total) {
        this.subjectId = id;
        this.subjectName = name;
        this.attended = attended;
        this.total = total;
    }
}
