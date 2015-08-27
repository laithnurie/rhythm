package com.laithlab.core.musicutil;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsRepo {

	final static String MEDIA_PATH = Environment.getExternalStorageDirectory()
			.getPath() + "/";
	private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private static String mp3Pattern = ".mp3";

	// Constructor
	public SongsRepo() {

	}

	/**
	 * Function to read all mp3 files and store the details in
	 * ArrayList
	 */
	public static ArrayList<HashMap<String, String>> getPlayList() {
		if (MEDIA_PATH != null) {
			File home = new File(MEDIA_PATH);
			File[] listFiles = home.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File file : listFiles) {
					System.out.println(file.getAbsolutePath());
					if (file.isDirectory()) {
						scanDirectory(file);
					} else {
						addSongToList(file);
					}
				}
			}
		}
		// return songs list array
		return songsList;
	}

	private static void scanDirectory(File directory) {
		if (directory != null) {
			File[] listFiles = directory.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File file : listFiles) {
					if (file.isDirectory()) {
						scanDirectory(file);
					} else {
						addSongToList(file);
					}

				}
			}
		}
	}

	private static void addSongToList(File song) {
		if (song.getName().endsWith(mp3Pattern)) {
			HashMap<String, String> songMap = new HashMap<String, String>();
			songMap.put("songTitle",
					song.getName().substring(0, (song.getName().length() - 4)));
			songMap.put("songPath", song.getPath());

			// Adding each song to SongList
			songsList.add(songMap);
		}
	}
}
