package com.laithlab.rhythm.utils;


import android.media.MediaMetadataRetriever;

public class MusicMetaData {

    private String artist;
    private String album;
    private String title;
    private byte[] albumArt;
    private Long duration;

    public MusicMetaData(String filePath) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

        try {
            metaRetriver.setDataSource(filePath);

            this.artist = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            this.album = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            this.title = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            this.albumArt = metaRetriver.getEmbeddedPicture();
            try {
                this.duration = Long.valueOf(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            } catch (Exception e) {
                this.duration = 0L;
            }
            metaRetriver.release();
        } catch (Exception e) {
            this.artist = "Unknown Artist";
            this.album = "Unknown Album";
            this.title = filePath;
            this.albumArt = null;
            this.duration = 0L;
            metaRetriver.release();
        }

    }

    public String getSongTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public byte[] getAlbumArt() {
        return albumArt;
    }

    public String getArtistName() {
        return artist;
    }

    public String getAlbumName() {
        return album;
    }

}