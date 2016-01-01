package com.laithlab.core.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.laithlab.core.utils.ContentType;

public class MusicContent implements Parcelable {

    private String playlistName;
    private String id;
    private ContentType contentType;

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playlistName);
        dest.writeString(this.id);
        dest.writeInt(this.contentType == null ? -1 : this.contentType.ordinal());
    }

    public MusicContent() {
    }

    protected MusicContent(Parcel in) {
        this.playlistName = in.readString();
        this.id = in.readString();
        int tmpContentType = in.readInt();
        this.contentType = tmpContentType == -1 ? null : ContentType.values()[tmpContentType];
    }

    public static final Parcelable.Creator<MusicContent> CREATOR = new Parcelable.Creator<MusicContent>() {
        public MusicContent createFromParcel(Parcel source) {
            return new MusicContent(source);
        }

        public MusicContent[] newArray(int size) {
            return new MusicContent[size];
        }
    };
}
