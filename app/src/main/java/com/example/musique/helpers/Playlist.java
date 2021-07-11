package com.example.musique.helpers;

import java.io.Serializable;

public class Playlist implements Serializable {

    String id;
    String name;
    String createdDate;

    public Playlist(String id, String name, String createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
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
