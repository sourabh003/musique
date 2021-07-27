package com.example.musique.helpers;

import java.io.Serializable;

public class Album implements Serializable {
    String songsCount;
    String artist;
    String name;
    String id;
    String art;

    public Album(String id, String name, String artist, String songsCount){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.songsCount = songsCount;
    }

    public Album(String id, String name, String artist, String songsCount, String art){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.songsCount = songsCount;
        this.art = art;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(String songsCount) {
        this.songsCount = songsCount;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
