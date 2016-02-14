package com.laithlab.rhythm.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class RhythmSong implements Parcelable {

    final private String artistTitle;
    final private String albumTitle;
    final private String trackTitle;
    final private byte[] imageData;
    final private long duration;
    final private String songLocation;

    private RhythmSong(RhythmSongBuilder builder) {
        this.artistTitle = builder.artistTitle;
        this.albumTitle = builder.albumTitle;
        this.trackTitle = builder.trackTitle;
        this.imageData = builder.imageData;
        this.duration = builder.duration;
        this.songLocation = builder.songLocation;
    }

    public String getArtistTitle() {
        return artistTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public long getDuration() {
        return duration;
    }

    public String getSongLocation() {
        return songLocation;
    }

    public static class RhythmSongBuilder {
        private String artistTitle;
        private String albumTitle;
        private String trackTitle;
        private byte[] imageData;
        private long duration;
        private String songLocation;

        public RhythmSongBuilder artistTitle(String artistTitle) {
            this.artistTitle = artistTitle;
            return this;
        }

        public RhythmSongBuilder albumTitle(String albumTitle) {
            this.albumTitle = albumTitle;
            return this;
        }

        public RhythmSongBuilder trackTitle(String trackTitle) {
            this.trackTitle = trackTitle;
            return this;
        }

        public RhythmSongBuilder imageData(byte[] imageData) {
            this.imageData = imageData;
            return this;
        }

        public RhythmSongBuilder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public RhythmSongBuilder songLocation(String songLocation) {
            this.songLocation = songLocation;
            return this;
        }

        public RhythmSong build() {
            return new RhythmSong(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistTitle);
        dest.writeString(this.albumTitle);
        dest.writeString(this.trackTitle);
        dest.writeByteArray(this.imageData);
        dest.writeFloat(this.duration);
        dest.writeString(this.songLocation);
    }

    protected RhythmSong(Parcel in) {
        this.artistTitle = in.readString();
        this.albumTitle = in.readString();
        this.trackTitle = in.readString();
        this.imageData = in.createByteArray();
        this.duration = in.readLong();
        this.songLocation = in.readString();
    }

    public static final Parcelable.Creator<RhythmSong> CREATOR = new Parcelable.Creator<RhythmSong>() {
        public RhythmSong createFromParcel(Parcel source) {
            return new RhythmSong(source);
        }

        public RhythmSong[] newArray(int size) {
            return new RhythmSong[size];
        }
    };

    @Override
    public String toString() {
        return "RhythmSong{" +
                "artistTitle='" + artistTitle + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", trackTitle='" + trackTitle + '\'' +
                ", duration=" + duration +
                ", songLocation='" + songLocation + '\'' +
                '}';
    }
}


