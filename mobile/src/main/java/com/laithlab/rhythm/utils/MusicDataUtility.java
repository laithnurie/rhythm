package com.laithlab.rhythm.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.laithlab.rhythm.db.Album;
import com.laithlab.rhythm.db.Artist;
import com.laithlab.rhythm.db.Playlist;
import com.laithlab.rhythm.db.Song;
import com.laithlab.rhythm.dto.MusicContent;
import com.laithlab.rhythm.dto.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MusicDataUtility {

    private static boolean isUpdating = false;

    // Constructor
    public MusicDataUtility() {
    }

    public static void getMusicContent(Context context) {
        getMusicContentByUri(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        getMusicContentByUri(context, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private static void getMusicContentByUri(Context context, Uri contentUri) {
        ContentResolver cr = context.getContentResolver();

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(contentUri, null, selection, null, sortOrder);
        int count;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    createSongEntry(context, data);
                }
            }
            cur.close();
        }
    }

    public static byte[] getImageData(String songLocation) {
        File songFile = new File(songLocation);
        if (songFile.exists()) {
            return new MusicMetaData(songLocation).getAlbumArt();
        }
        return null;
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

    public static void updateMusicDB(Context context) {
        if (!isUpdating) {
            isUpdating = true;
            getMusicContent(context);
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
        realm.close();
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

    public static List<Song> getLastPlayedSongs(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<Song> result = realm.where(Song.class).greaterThan("lastPlayed", 0).findAll();
        result.sort("lastPlayed", Sort.DESCENDING);
        return result;
    }

    public static List<Song> getMostPlayedSongs(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<Song> result = realm.where(Song.class).greaterThan("noOfPlayed", 0).findAll();
        result.sort("noOfPlayed", Sort.DESCENDING);
        return result;
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
        realm.close();
        return song;
    }

    public static Album getAlbumById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Album album = realm.where(Album.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        realm.close();
        return album;
    }

    public static Artist getArtistById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Artist artist = realm.where(Artist.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        realm.close();
        return artist;
    }

    public static Playlist getPlaylistById(String id, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Playlist playlist = realm.where(Playlist.class)
                .contains("id", id)
                .findFirst();
        realm.commitTransaction();
        realm.close();
        return playlist;
    }

    public static List<Song> getSongsFromList(MusicContent musicContent, Context context) {
        switch (musicContent.getContentType()) {
            case ARTIST:
                //TODO: get all songs from artist
                return getArtistById(musicContent.getId(), context).getAlbums().get(0).getSongs();
            case ALBUM:
                return getAlbumById(musicContent.getId(), context).getSongs();
            case PLAYLIST:
                return getPlaylistById(musicContent.getId(), context).getSongs();
            case MOST_PLAYED:
                return getMostPlayedSongs(context);
            case LAST_PLAYED:
                return getLastPlayedSongs(context);
        }

        return null;
    }

    public static void createPlaylist(String playlistName, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();

        Playlist playlistRecord = realm.createObject(Playlist.class);
        playlistRecord.setId(UUID.randomUUID().toString());
        playlistRecord.setPlaylistName(playlistName);

        realm.commitTransaction();
        realm.close();
    }

    public static void deletePlaylist(String playlistID, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();

        Playlist playlistToDelete = realm.where(Playlist.class)
                .equalTo("id", playlistID)
                .findFirst();
        playlistToDelete.removeFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static void updateSongCount(String songId, Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        Song song = realm.where(Song.class)
                .contains("id", songId)
                .findFirst();
        song.setNoOfPlayed(song.getNoOfPlayed() + 1);
        song.setLastPlayed(System.currentTimeMillis());

        realm.commitTransaction();
        realm.close();
    }

    public static void resetMusicStats(Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.where(Artist.class).findAll().clear();
        realm.where(Album.class).findAll().clear();
        realm.where(Song.class).findAll().clear();
        realm.where(Playlist.class).findAll().clear();
        realm.commitTransaction();
        realm.close();
    }
}
