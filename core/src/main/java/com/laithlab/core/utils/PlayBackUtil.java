package com.laithlab.core.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.List;

import com.laithlab.core.dto.SongDTO;

public class PlayBackUtil {
    private static List<SongDTO> currentPlayList = null;
    private static int currentSongPosition = 0;
    private static MediaPlayer mediaPlayerService = null;
    private static PlayMode currentPlayMode = PlayMode.NONE;
    private static RhythmSong currentSong;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayerService;
    }

    public static MediaPlayer setMediaPlayerOne(Context context, String songLocation) {
        try {
            if (mediaPlayerService != null) {
                if (mediaPlayerService.isPlaying()) {
                    mediaPlayerService.stop();
                }
                mediaPlayerService.reset();
            }
            mediaPlayerService = MediaPlayer.create(context, Uri.parse(songLocation));
            mediaPlayerService.setAudioStreamType(AudioManager.STREAM_MUSIC);
            return mediaPlayerService;
        } catch (Exception e) {
            mediaPlayerService = MediaPlayer.create(context, Uri.parse(songLocation));
            mediaPlayerService.setAudioStreamType(AudioManager.STREAM_MUSIC);
            return mediaPlayerService;
        }
    }

    public static List<SongDTO> getCurrentPlayList() {
        return currentPlayList;
    }

    public static void setCurrentPlayList(List<SongDTO> newPlayList) {
        currentPlayList = newPlayList;
    }

    public static int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public static void setCurrentSongPosition(int newCurrentPosition) {
        currentSongPosition = newCurrentPosition;
    }

    public static PlayMode getCurrentPlayMode() {
        return currentPlayMode;
    }

    public static PlayMode getUpdateCurrentPlayMode(PlayMode newPlayMode) {
        if (newPlayMode == PlayMode.SHUFFLE) {
            if (currentPlayMode == PlayMode.NONE || currentPlayMode == PlayMode.SINGLE_REPEAT) {
                currentPlayMode = PlayMode.SHUFFLE;
            } else if (currentPlayMode == PlayMode.ALL_REPEAT) {
                currentPlayMode = PlayMode.SHUFFLE_REPEAT;
            } else if (currentPlayMode == PlayMode.SHUFFLE_REPEAT) {
                currentPlayMode = PlayMode.ALL_REPEAT;
            } else if (currentPlayMode == PlayMode.SHUFFLE) {
                currentPlayMode = PlayMode.NONE;
            }
        } else if (newPlayMode == PlayMode.REPEAT) {
            if (currentPlayMode == PlayMode.NONE) {
                currentPlayMode = PlayMode.SINGLE_REPEAT;
            } else if (currentPlayMode == PlayMode.SINGLE_REPEAT) {
                currentPlayMode = PlayMode.ALL_REPEAT;
            } else if (currentPlayMode == PlayMode.ALL_REPEAT) {
                currentPlayMode = PlayMode.NONE;
            } else if (currentPlayMode == PlayMode.SHUFFLE) {
                currentPlayMode = PlayMode.SHUFFLE_REPEAT;
            } else if (currentPlayMode == PlayMode.SHUFFLE_REPEAT) {
                currentPlayMode = PlayMode.SHUFFLE;
            }
        }
        return currentPlayMode;
    }

    public static RhythmSong getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(RhythmSong currentSong) {
        PlayBackUtil.currentSong = currentSong;
    }
}
