package com.laithlab.rhythm.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Album extends RealmObject {

    private String id;
    private String artistId;
    private String albumTitle;
    private RealmList<Song> songs;
    private String coverPath;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
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
