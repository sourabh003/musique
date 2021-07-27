package com.example.musique.helpers;

import java.io.Serializable;

public class Playlist implements Serializable {

    String id;
    String name;
    String createdDate;
    String image;

    public Playlist() {
    }

    public Playlist(String id, String name, String createdDate, String image) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
