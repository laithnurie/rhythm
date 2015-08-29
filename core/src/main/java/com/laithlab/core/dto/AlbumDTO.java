package com.laithlab.core.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AlbumDTO implements Parcelable {

	private String albumTitle;
	private String albumImageUrl;
	private List<SongDTO> songs;


	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumImageUrl(String albumImageUrl) {
		this.albumImageUrl = albumImageUrl;
	}

	public String getAlbumImageUrl() {
		return albumImageUrl;
	}

	public void setSongs(List<SongDTO> songs) {
		this.songs = songs;
	}

	public List<SongDTO> getSongs() {
		return songs;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.albumTitle);
		dest.writeString(this.albumImageUrl);
		dest.writeTypedList(this.songs);
	}

	public AlbumDTO() {
	}

	protected AlbumDTO(Parcel in) {
		this.albumTitle = in.readString();
		this.albumImageUrl = in.readString();
		this.songs = new ArrayList<SongDTO>();
		in.readTypedList(this.songs, SongDTO.CREATOR);
	}

	public static final Parcelable.Creator<AlbumDTO> CREATOR = new Parcelable.Creator<AlbumDTO>() {
		public AlbumDTO createFromParcel(Parcel source) {
			return new AlbumDTO(source);
		}

		public AlbumDTO[] newArray(int size) {
			return new AlbumDTO[size];
		}
	};
}
