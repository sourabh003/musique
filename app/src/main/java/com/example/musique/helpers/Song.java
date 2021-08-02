package com.example.musique.helpers;

public class Song {
    String id;
    String title;
    String artist;
    String album;
    String data;
    String albumId;

    public Song(String id, String title, String artist, String album, String data) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
    }

    public Song(String id, String title, String artist, String album, String data, String albumId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumId = albumId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
