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

	public static MediaPlayer getMediaPlayer() {
		return mediaPlayerService;
	}

	public static MediaPlayer setMediaPlayerOne(Context context, String songLocation){
		try {
			if(mediaPlayerService != null){
				if(mediaPlayerService.isPlaying()){
					mediaPlayerService.stop();
				}
				mediaPlayerService.reset();
			}
			mediaPlayerService = MediaPlayer.create(context, Uri.parse(songLocation));
			mediaPlayerService.setAudioStreamType(AudioManager.STREAM_MUSIC);
			return mediaPlayerService;
		} catch (Exception e){
			mediaPlayerService = MediaPlayer.create(context, Uri.parse(songLocation));
			mediaPlayerService.setAudioStreamType(AudioManager.STREAM_MUSIC);
			return mediaPlayerService;
		}
	}

	public static List<SongDTO> getCurrentPlayList(){
		return currentPlayList;
	}

	public static void setCurrentPlayList(List<SongDTO> newPlayList){
		currentPlayList = newPlayList;
	}

	public static int getCurrentSongPosition(){
		return currentSongPosition;
	}

	public static void setCurrentSongPosition(int newCurrentPosition){
		currentSongPosition = newCurrentPosition;
	}
}
