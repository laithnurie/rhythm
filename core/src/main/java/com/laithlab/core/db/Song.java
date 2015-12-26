package com.laithlab.core.db;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Song extends RealmObject {
    private String id;
    private String songTitle;
    private long songDuration;
    private String songLocation;
    private String albumId;

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
}
