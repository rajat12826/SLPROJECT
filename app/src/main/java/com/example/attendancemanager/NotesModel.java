package com.example.attendancemanager;

public class NotesModel {
    private int id;
    private String title;
    private String content;
    private String timestamp;

    public NotesModel(int id, String title, String content, String timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }



    public String getContent() {
        return content;
    }


    public String getTimestamp() {
        return timestamp;
    }


}