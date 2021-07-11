package com.example.musique.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.musique.helpers.Song;
import com.example.musique.utility.Functions;

import java.util.ArrayList;
import java.util.List;

public class PlayerService {
    public static Song currentSong = null;
    public static MediaPlayer mediaPlayer = null;
    public static boolean isLibraryRepeating = false;
    public static ArrayList<Song> tracksList = new ArrayList<>();
    public static int songIndex = 0;
    public static List<String> favouriteSongsList = new ArrayList<>();

    public static void nextSong() {
        if (songIndex == tracksList.size() - 1) {
            songIndex = 0;
        } else {
            songIndex++;
        }
        currentSong = tracksList.get(songIndex);
    }

    public static void previousSong() {
        if (songIndex == 0) {
            songIndex = tracksList.size() - 1;
        } else {
            songIndex--;
        }
        currentSong = tracksList.get(songIndex);
    }

    public static void loadMediaPlayer(Context ctx) {
        final Context context = ctx;
        if (Functions.getLatestSong(context).getId().equals(currentSong.getId())) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, Uri.parse(currentSong.getData()));
                mediaPlayer.setOnCompletionListener(mp -> {
                    if (isLibraryRepeating) {
                        nextSong();
                        loadMediaPlayer(context);
                    } else {
                        if (!mediaPlayer.isLooping()) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.pause();
                        }
                    }
                });
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                });
            } else {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, Uri.parse(currentSong.getData()));
            mediaPlayer.setOnCompletionListener(mp -> {
                if (isLibraryRepeating) {
                    nextSong();
                    loadMediaPlayer(context);
                } else {
                    if (!mediaPlayer.isLooping()) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.pause();
                    }
                }
            });
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
            });
        }
        Functions.updateLatestSong(context, currentSong);
    }

    public static void playAllSongs(Context context, ArrayList<Song> list) {
        tracksList.clear();
        tracksList.addAll(list);
        songIndex = 0;
        currentSong = tracksList.get(songIndex);
        loadMediaPlayer(context);
    }
}
