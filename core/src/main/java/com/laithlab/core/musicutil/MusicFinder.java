package com.laithlab.core.musicutil;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import com.laithlab.core.db.Album;
import com.laithlab.core.db.Artist;
import com.laithlab.core.db.Song;
import io.realm.Realm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicFinder {

	final static String MEDIA_PATH = Environment.getExternalStorageDirectory()
			.getPath() + "/";
	private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private static String mp3Pattern = ".mp3";

	// Constructor
	public MusicFinder() {
	}

	/**
	 * Function to read all mp3 files and store the details in
	 * ArrayList
	 */
	public static ArrayList<HashMap<String, String>> getSongList() {
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

	public static void updateMusicDB(Context context) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		for (final HashMap<String, String> song : getSongList()) {
			mmr.setDataSource(song.get("songPath"));
			createSongRecord(mmr, context, song.get("songPath"));
		}
	}

	private static void createSongRecord(MediaMetadataRetriever mmr, Context context, String songPath) {
		final Realm realm = Realm.getInstance(context);

		final String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		final String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		final String trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		final String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

		realm.beginTransaction();
		Artist artistRecord = getOrCreateArtist(realm, artist);
		Album albumRecord = getOrCreateAlbum(realm, artistRecord, album);
		getOrCreateSong(realm, albumRecord, trackTitle, duration, songPath);
		realm.commitTransaction();

	}

	private static Artist getOrCreateArtist(Realm realm, String artist) {
		if (artist == null) {
			Artist query = realm.where(Artist.class)
					.contains("artistName", "Untitled Artist")
					.findFirst();
			if (query != null) {
				return query;
			} else {
				Artist newArtist = realm.createObject(Artist.class);
				newArtist.setArtistName("Untitled Artist");
				return newArtist;
			}
		} else {
			Artist query = realm.where(Artist.class)
					.contains("artistName", artist)
					.findFirst();
			if (query != null) {
				return query;
			} else {
				Artist newArtist = realm.createObject(Artist.class);
				newArtist.setArtistName(artist);
				return newArtist;
			}
		}
	}

	private static Album getOrCreateAlbum(Realm realm, Artist artistRecord, String albumTitle) {
		Album albumRecord = null;
		if (albumTitle == null) {
			for (Album albumItem : artistRecord.getAlbums()) {
				if (albumItem.getAlbumTitle().equals("Untitled Album")) {
					albumRecord = albumItem;
				}
			}
			if (albumRecord != null) {
				return albumRecord;
			} else {
				albumRecord = realm.createObject(Album.class);
				albumRecord.setAlbumTitle("Untitled Album");
				artistRecord.getAlbums().add(albumRecord);
				return albumRecord;
			}
		} else {
			for (Album albumItem : artistRecord.getAlbums()) {
				if (albumItem.getAlbumTitle().equals(albumTitle)) {
					albumRecord = albumItem;
				}
			}
			if (albumRecord != null) {
				return albumRecord;
			} else {
				albumRecord = realm.createObject(Album.class);
				albumRecord.setAlbumTitle(albumTitle);
				artistRecord.getAlbums().add(albumRecord);
				return albumRecord;
			}
		}
	}

	private static void getOrCreateSong(Realm realm, Album albumRecord, String songTitle, String duration, String songPath) {
		Song songRecord = null;
		for (Song songItem : albumRecord.getSongs()) {
			if (songItem.getSongLocation().equals(songPath)) {
				songRecord = songItem;
			}
		}
		if (songRecord == null) {
			songRecord = realm.createObject(Song.class);
			songRecord.setSongTitle(songTitle != null ? songTitle : "Untitle Song");
			songRecord.setSongLocation(songPath);
			if (duration != null) {
				songRecord.setSongDuration(Integer.parseInt(duration));
			}
			albumRecord.getSongs().add(songRecord);
		}

	}


	public static List<Artist> allArtists(Context context) {
		Realm realm = Realm.getInstance(context);
		return realm.allObjects(Artist.class);
	}
}
