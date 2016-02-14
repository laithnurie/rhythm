package com.laithlab.rhythm.fragment;


public interface SongFragmentCallback {
    void changePlayerStyle(int vibrantColor, int songPosition);

    void setToolBarText(String artistTitle, String albumTitle);

    void resetChangedSongFromNotification();

    boolean songChangedFromNotification();

    void playNext();
}
