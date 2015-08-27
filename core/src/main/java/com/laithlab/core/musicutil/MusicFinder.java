package com.laithlab.core.musicutil;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import com.laithlab.core.RestAdapterFactory;
import com.laithlab.core.db.Album;
import com.laithlab.core.db.Artist;
import com.laithlab.core.db.Song;
import com.laithlab.core.echonest.EchoNestApi;
import com.laithlab.core.echonest.EchoNestSearch;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
		final Realm realm = Realm.getInstance(context);
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();

		final EchoNestApi echoNestApi = RestAdapterFactory.getEchoNestApi();

		for (final HashMap<String, String> song : getSongList()) {
			mmr.setDataSource(song.get("songPath"));
			final String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			final String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			final String trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			final String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

			Log.v("lnln", "Artist - " + artist + " Album - " + album + " Title - " + trackTitle);

			if (artist != null && !artist.isEmpty()) {
				echoNestApi.getArtistImage(artist, new Callback<EchoNestSearch>() {
					@Override
					public void success(EchoNestSearch echoNestSearch, Response response) {
						if (echoNestSearch.getResponse() != null && echoNestSearch.getResponse().trackImage() != null) {
							RealmResults<Artist> allRex = realm.where(Artist.class)
									.contains("artistName", artist)
									.findAll();
							if (allRex.size() > 0) {

							} else {
								realm.beginTransaction();
								final Artist artistRecord = realm.createObject(Artist.class);
								artistRecord.setArtistName(artist);
								artistRecord.setArtistImageUrl(echoNestSearch.getResponse().trackImage());

								final Album newAlbum = realm.createObject(Album.class);
								newAlbum.setAlbumTitle(album != null ? album : "Untitled");
								if (trackTitle != null) {
									echoNestApi.getSongImage(artist, trackTitle, new Callback<EchoNestSearch>() {
										@Override
										public void success(EchoNestSearch echoNestSearch, Response response) {
											if (echoNestSearch.getResponse() != null && echoNestSearch.getResponse().trackImage() != null) {
												Song songRow = realm.createObject(Song.class);
												songRow.setSongDirectory(song.get("songPath"));
												songRow.setSongImageUrl(echoNestSearch.getResponse().trackImage());
												songRow.setSongTitle(trackTitle);
												songRow.setSongDuration(Integer.parseInt(duration));
												newAlbum.getSongs().add(songRow);
												artistRecord.getAlbums().add(newAlbum);
											}
										}

										@Override
										public void failure(RetrofitError error) {

										}
									});
								} else {
									Song songRow = realm.createObject(Song.class);
									songRow.setSongDirectory(song.get("songPath"));
									songRow.setSongTitle("Untitled");
									newAlbum.getSongs().add(songRow);
									artistRecord.getAlbums().add(newAlbum);
								}
								realm.commitTransaction();
							}
						}
					}

					@Override
					public void failure(RetrofitError error) {
						error.getBody();
					}
				});
			}

		}
	}

	public static RealmResults<Artist> allArtists(Context context) {
		Realm realm = Realm.getInstance(context);
		return realm.allObjects(Artist.class);
	}
}
