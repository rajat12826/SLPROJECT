package com.example.attendancemanager;
public class Student {
    int id;
    String name;
    String roll;

    public Student(int id, String name, String roll) {
        this.id = id;
        this.name = name;
        this.roll = roll;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRoll() { return roll; }
}
