package com.example.sjecnotify.Model;

public class Notice
{
    private String title,date,description,image;

    public Notice()
    {

    }

    public Notice(String title, String date, String description,String image) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
