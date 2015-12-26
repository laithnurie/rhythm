package com.laithlab.core.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AlbumDTO implements Parcelable {

    private String id;
    private String artistId;
    private String albumTitle;
    private String albumImageUrl;
    private List<SongDTO> songs;
    private String coverPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumTitle);
        dest.writeString(this.albumImageUrl);
        dest.writeTypedList(this.songs);
        dest.writeString(this.id);
        dest.writeString(this.artistId);
        dest.writeString(this.coverPath);
    }

    public AlbumDTO() {
    }

    protected AlbumDTO(Parcel in) {
        this.albumTitle = in.readString();
        this.albumImageUrl = in.readString();
        this.songs = new ArrayList<>();
        in.readTypedList(this.songs, SongDTO.CREATOR);
        this.id = in.readString();
        this.artistId = in.readString();
        this.coverPath = in.readString();
    }

    public static final Parcelable.Creator<AlbumDTO> CREATOR = new Parcelable.Creator<AlbumDTO>() {
        public AlbumDTO createFromParcel(Parcel source) {
            return new AlbumDTO(source);
        }

        public AlbumDTO[] newArray(int size) {
            return new AlbumDTO[size];
        }
    };
}
