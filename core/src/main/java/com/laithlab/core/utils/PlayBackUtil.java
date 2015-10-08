package com.laithlab.core.utils;

import android.media.MediaPlayer;
import com.laithlab.core.dto.SongDTO;

import java.util.List;

public class PlayBackUtil {

	private static List<SongDTO> currentPlayList = null;
	private static int currentSongPosition = 0;

	private static MediaPlayer mediaPlayer = null;

	public static MediaPlayer getMediaPlayer() {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		return mediaPlayer;
	}

	public static List<SongDTO> getCurrentPlayList(){
		return currentPlayList;
	}

	public static void setPlayList(List<SongDTO>newPlayList){
		currentPlayList = newPlayList;
	}

	public static int getCurrentSongPosition(){
		return currentSongPosition;
	}

	public static void setCurrentSongPosition(int newCurrentPosition){
		currentSongPosition = newCurrentPosition;
	}
}
