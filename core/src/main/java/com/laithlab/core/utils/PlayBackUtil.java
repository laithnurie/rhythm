package com.laithlab.core.utils;

import android.media.MediaPlayer;
import com.laithlab.core.dto.SongDTO;

import java.util.List;

public class PlayBackUtil {

	private static List<SongDTO> currentPlayList = null;
	private static int currentSongPosition = 0;

	private static MediaPlayer mediaPlayerService = null;

	public static MediaPlayer getMediaPlayer() {
		return mediaPlayerService;
	}

	public static MediaPlayer setMediaPlayer(MediaPlayer mediaPlayer) {
		mediaPlayerService = mediaPlayer;
		return mediaPlayerService;
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
