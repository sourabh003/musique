package com.example.musique.service;

import android.media.MediaPlayer;

import com.example.musique.helpers.Song;

import java.util.ArrayList;

public class PlayerService {
    public static Song currentSong = null;
    public static MediaPlayer mediaPlayer = null;
    public static boolean isLibraryRepeating = false;
    public static ArrayList<Song> tracksList = new ArrayList<>();
    public static int songIndex = 0;

    public static void nextSong(){
        if (songIndex == tracksList.size() - 1){
            songIndex = 0 ;
        } else {
            songIndex++;
        }
        currentSong = tracksList.get(songIndex);
    }

    public static void previousSong(){
        if (songIndex == 0){
            songIndex = tracksList.size() - 1  ;
        } else {
            songIndex--;
        }
        currentSong = tracksList.get(songIndex);
    }
}
