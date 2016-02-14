package com.laithlab.rhythm.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Playlist extends RealmObject {
    private String id;
    private String playlistName;
    private RealmList<Song> songs;
    private String coverPath;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistName() {
        return playlistName;
    }


    public void setSongs(RealmList<Song> songs) {
        this.songs = songs;
    }

    public RealmList<Song> getSongs() {
        return songs;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
