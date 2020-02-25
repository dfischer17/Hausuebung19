package com.example.notesmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private LocalDateTime time;
    private String name;

    public Note(LocalDateTime time, String name) {
        this.time = time;
        this.name = name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String toCsvString() {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ";" + name;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }
}
