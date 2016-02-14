package com.laithlab.rhythm.db;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Song extends RealmObject {
    private String id;
    private String songTitle;
    private long songDuration;
    private String songLocation;
    private String albumId;
    private int noOfPlayed;
    private long lastPlayed;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongDuration(long songDuration) {
        this.songDuration = songDuration;
    }

    public long getSongDuration() {
        return songDuration;
    }

    public void setSongLocation(String songLocation) {
        this.songLocation = songLocation;
    }

    public String getSongLocation() {
        return songLocation;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setNoOfPlayed(int noOfPlayed) {
        this.noOfPlayed = noOfPlayed;
    }

    public int getNoOfPlayed() {
        return noOfPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }
}
