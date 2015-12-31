package com.laithlab.core.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.laithlab.core.db.Album;
import com.laithlab.core.db.Artist;
import com.laithlab.core.db.Playlist;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.SearchResult;

import io.realm.Realm;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class MusicDataUtility {

    private static ArrayList<HashMap<String, String>> songsList = new ArrayList<>();
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    private static String mp3Pattern = ".mp3";
    private static boolean isUpdating = false;

    // Constructor
    public MusicDataUtility() {
    }

    public static ArrayList<HashMap<String, String>> getMusicFromStorage() {
        for (String storage : getStorageDirectories()) {
            getSongList(storage);
        }
        return songsList;
    }

    public static byte[] getImageData(String songLocation) {
        MusicMetaData musicMetaData = new MusicMetaData(songLocation);
        return musicMetaData.getAlbumArt();
    }

    public static RhythmSong getSongMeta(String songLocation) {
        MusicMetaData musicMetaData = new MusicMetaData(songLocation);
        String artist = musicMetaData.getArtistName() != null ? musicMetaData.getArtistName() : "Unknown Artist";
        String album = musicMetaData.getAlbumName() != null ? musicMetaData.getAlbumName() : "Unknown Album";
        String track = musicMetaData.getSongTitle() != null ? musicMetaData.getSongTitle() : "Unknown Track";
        byte[] imageData = musicMetaData.getAlbumArt();
        long duration = musicMetaData.getDuration() / 1000;
        return new RhythmSong.RhythmSongBuilder().songLocation(songLocation).artistTitle(artist).albumTitle(album)
                .trackTitle(track).imageData(imageData).duration(duration).build();
    }

    private static void getSongList(String path) {

        if (path != null) {
            File home = new File(path);
            File[] listFiles = home.listFiles();
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
        // return songs list array
    }

    public static List<SearchResult> getAllSearchResults(Context context) {
        List<SearchResult> results = new ArrayList<>();
        List<Artist> artists = allArtists(context);
        for (Artist artist : artists) {
            results.add(createSearchResult(artist.getId(), artist.getArtistName(), artist.getAlbums().size() + " albums", SearchResult.ResultType.ARTIST));
            for (Album album : artist.getAlbums()) {
                results.add(createSearchResult(album.getId(), album.getAlbumTitle(), artist.getArtistName(), SearchResult.ResultType.ALBUM));
                for (Song song : album.getSongs()) {
                    results.add(createSearchResult(song.getId(), song.getSongTitle(),
                            artist.getArtistName() + " - " + album.getAlbumTitle()
                            , SearchResult.ResultType.SONG));
                }
            }
        }
        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult lhs, SearchResult rhs) {
                return lhs.getResultType().compareTo(rhs.getResultType());
            }
        });
        int lastIndexOfArtists = 0;
        int lastIndexOfAlbums = 0;
        SearchResult.ResultType resultType;
        for (int i = 0; i < results.size(); i++) {
            resultType = results.get(i).getResultType();
            if (resultType == SearchResult.ResultType.ARTIST) {
                lastIndexOfArtists = i;
            } else if (resultType == SearchResult.ResultType.ALBUM) {
                lastIndexOfAlbums = i;
            }
        }
        results.add(0, createSearchResult(null, "Artists", null, SearchResult.ResultType.HEADER));
        results.add(lastIndexOfArtists + 2, createSearchResult(null, "Albums", null, SearchResult.ResultType.HEADER));
        results.add(lastIndexOfAlbums + 3, createSearchResult(null, "Songs", null, SearchResult.ResultType.HEADER));
        return results;
    }

    private static SearchResult createSearchResult(String id, String mainTitle, String subTitle, SearchResult.ResultType resultType) {
        return new SearchResult.SearchResultBuilder().id(id).mainTitle(mainTitle).subTitle(subTitle).setResultType(resultType).build();
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
            HashMap<String, String> songMap = new HashMap<>();
            songMap.put("songTitle",
                    song.getName().substring(0, (song.getName().length() - 4)));
            songMap.put("songPath", song.getPath());

            songsList.add(songMap);
        }
    }

    public static void updateMusicDB(Context context) {
        if (!isUpdating) {
            isUpdating = true;
            for (final HashMap<String, String> song : getMusicFromStorage()) {
                createSongEntry(context, song.get("songPath"));
            }
            isUpdating = false;
        }
    }

    private static void createSongEntry(Context context, String songPath) {
        RhythmSong songEntry = getSongMeta(songPath);

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Artist artistRecord = getOrCreateArtist(realm, songEntry);
        Album albumRecord = getOrCreateAlbum(realm, artistRecord, songEntry);
        getOrCreateSong(realm, albumRecord, songEntry.getTrackTitle(), songEntry.getDuration(), songPath);
        realm.commitTransaction();

    }

    private static Artist getOrCreateArtist(Realm realm, RhythmSong rhythmSong) {
        if (rhythmSong.getArtistTitle().equals("Untitled Artist")) {
            Artist query = realm.where(Artist.class)
                    .contains("artistName", "Untitled Artist")
                    .findFirst();
            if (query != null) {
                return query;
            } else {
                Artist newArtist = realm.createObject(Artist.class);
                newArtist.setArtistName("Untitled Artist");
                if (rhythmSong.getImageData() != null) {
                    newArtist.setCoverPath(rhythmSong.getSongLocation());
                }
                newArtist.setId(UUID.randomUUID().toString());
                return newArtist;
            }
        } else {
            Artist query = realm.where(Artist.class)
                    .contains("artistName", rhythmSong.getArtistTitle())
                    .findFirst();
            if (query != null) {
                return query;
            } else {
                Artist newArtist = realm.createObject(Artist.class);
                newArtist.setArtistName(rhythmSong.getArtistTitle());
                if (rhythmSong.getImageData() != null) {
                    newArtist.setCoverPath(rhythmSong.getSongLocation());
                }
                newArtist.setId(UUID.randomUUID().toString());
                return newArtist;
            }
        }
    }

    private static Album getOrCreateAlbum(Realm realm, Artist artistRecord, RhythmSong rhythmSong) {
        Album albumRecord = null;
        if (rhythmSong.getAlbumTitle() == null) {
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
                albumRecord.setId(UUID.randomUUID().toString());
                albumRecord.setArtistId(artistRecord.getId());
                if (rhythmSong.getImageData() != null) {
                    albumRecord.setCoverPath(rhythmSong.getSongLocation());
                }
                artistRecord.getAlbums().add(albumRecord);
                return albumRecord;
            }
        } else {
            for (Album albumItem : artistRecord.getAlbums()) {
                if (albumItem.getAlbumTitle().equals(rhythmSong.getAlbumTitle())) {
                    albumRecord = albumItem;
                }
            }
            if (albumRecord != null) {
                return albumRecord;
            } else {
                albumRecord = realm.createObject(Album.class);
                albumRecord.setAlbumTitle(rhythmSong.getAlbumTitle());
                albumRecord.setId(UUID.randomUUID().toString());
                albumRecord.setArtistId(artistRecord.getId());
                if (rhythmSong.getImageData() != null) {
                    albumRecord.setCoverPath(rhythmSong.getSongLocation());
                }
                artistRecord.getAlbums().add(albumRecord);
                return albumRecord;
            }
        }
    }

    private static void getOrCreateSong(Realm realm, Album albumRecord, String songTitle, long duration, String songPath) {
        Song songRecord = null;
        for (Song songItem : albumRecord.getSongs()) {
            if (songItem.getSongLocation().equals(songPath)) {
                songRecord = songItem;
            }
        }
        if (songRecord == null) {
            songRecord = realm.createObject(Song.class);
            songRecord.setId(UUID.randomUUID().toString());
            songRecord.setSongTitle(songTitle != null ? songTitle : "Untitled Song");
            songRecord.setAlbumId(albumRecord.getId());
            songRecord.setSongLocation(songPath);
            if (duration > 0) {
                songRecord.setSongDuration(duration);
            }
            albumRecord.getSongs().add(songRecord);
        }

    }

    public static List<Artist> allArtists(Context context) {
        Realm realm = Realm.getInstance(context);
        return realm.allObjects(Artist.class);
    }

    public static List<Song> getAllSongs(Context context) {
        Realm realm = Realm.getInstance(context);
        return realm.allObjects(Song.class);
    }

    public static List<Playlist> getPlayists(Context context) {
        Realm realm = Realm.getInstance(context);
        return realm.allObjects(Playlist.class);
    }

    public static Song getSongById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Song song = realm.where(Song.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        return song;
    }

    public static Album getAlbumById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Album album = realm.where(Album.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        return album;
    }

    public static Artist getArtistById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Artist artist = realm.where(Artist.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        return artist;
    }

    public static void createPlaylist(String playlistName, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();

        Playlist playlistRecord = realm.createObject(Playlist.class);
        playlistRecord.setId(UUID.randomUUID().toString());
        playlistRecord.setPlaylistName(playlistName);

        realm.commitTransaction();
    }

    public static String[] getStorageDirectories() {
        // Final set of paths
        final Set<String> rv = new HashSet<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    public static String secondsToTimer(long totalSeconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (totalSeconds / (60 * 60));
        int minutes = (int) (totalSeconds % (60 * 60)) / (60);
        int seconds = (int) ((totalSeconds % (60 * 60)) % (60));
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
