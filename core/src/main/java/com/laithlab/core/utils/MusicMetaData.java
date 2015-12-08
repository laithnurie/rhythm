package com.laithlab.core.utils;


import android.media.MediaMetadataRetriever;

public class MusicMetaData {

    private MediaMetadataRetriever metaRetriver;

    public MusicMetaData(String filePath) {
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(filePath);
    }

    public String getSongTitle() {
        return metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    public long getDuration() {
        String durationStr = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.valueOf(durationStr);
    }

    public byte[] getAlbumArt() {
        return metaRetriver.getEmbeddedPicture();
    }

    public String getArtistName() {
        return metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }

    public String getAlbumName() {
        return metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

}