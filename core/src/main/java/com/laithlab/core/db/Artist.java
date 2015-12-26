package com.laithlab.core.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Artist extends RealmObject {
    private String id;
    private String artistName;
    private String artistImageUrl;
    private RealmList<Album> albums;
    private String coverPath;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistImageUrl(String artistImageUrl) {
        this.artistImageUrl = artistImageUrl;
    }

    public String getArtistImageUrl() {
        return artistImageUrl;
    }

    public void setAlbums(RealmList<Album> albums) {
        this.albums = albums;
    }

    public RealmList<Album> getAlbums() {
        return albums;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
