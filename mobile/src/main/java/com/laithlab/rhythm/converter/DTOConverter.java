package com.laithlab.rhythm.converter;

import com.laithlab.rhythm.db.Album;
import com.laithlab.rhythm.db.Artist;
import com.laithlab.rhythm.db.Song;
import com.laithlab.rhythm.dto.AlbumDTO;
import com.laithlab.rhythm.dto.ArtistDTO;
import com.laithlab.rhythm.dto.SongDTO;

import java.util.ArrayList;
import java.util.List;

public class DTOConverter {


    public static List<ArtistDTO> getArtistList(List<Artist> artists) {
        List<ArtistDTO> artistDTOs = new ArrayList<>();
        for (Artist artist : artists) {
            artistDTOs.add(getArtistDTO(artist));
        }
        return artistDTOs;
    }

    public static ArtistDTO getArtistDTO(Artist artist) {
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setArtistName(artist.getArtistName());
        artistDTO.setCoverPath(artist.getCoverPath());
        artistDTO.setAlbums(getAlbumList(artist.getAlbums()));
        artistDTO.setId(artist.getId());
        return artistDTO;
    }


    private static List<AlbumDTO> getAlbumList(List<Album> albums) {
        List<AlbumDTO> albumDTOs = new ArrayList<>();
        for (Album album : albums) {
            albumDTOs.add(getAlbumDTO(album));
        }
        return albumDTOs;
    }

    private static AlbumDTO getAlbumDTO(Album album) {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setAlbumTitle(album.getAlbumTitle());
        albumDTO.setCoverPath(album.getCoverPath());
        albumDTO.setSongs(getSongList(album.getSongs()));
        albumDTO.setId(album.getId());
        albumDTO.setArtistId(album.getId());
        return albumDTO;
    }

    public static List<SongDTO> getSongList(List<Song> songs) {
        List<SongDTO> songDTOs = new ArrayList<>();
        for (Song song : songs) {
            songDTOs.add(getSongDTO(song));
        }
        return songDTOs;
    }

    public static SongDTO getSongDTO(Song song) {
        SongDTO songDTO = new SongDTO();
        songDTO.setSongTitle(song.getSongTitle());
        songDTO.setSongDuration(song.getSongDuration());
        songDTO.setSongLocation(song.getSongLocation());
        songDTO.setAlbumId(song.getAlbumId());
        songDTO.setId(song.getId());
        return songDTO;
    }
}
