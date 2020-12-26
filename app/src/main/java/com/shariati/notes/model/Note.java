package com.shariati.notes.model;

public class Note {
    private String title;
    private String note;
    private Long creation_date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Long creation_date) {
        this.creation_date = creation_date;
    }

    public Note(){}

    public Note(String title, String note, Long creationDate) {
        this.title = title;
        this.note = note;
        this.creation_date = creationDate;
    }



}
