package com.laithlab.core.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ArtistDTO implements Parcelable {

	private String id;
	private String artistName;
	private List<AlbumDTO> albumDTOList;
	private String coverPath;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setAlbums(List<AlbumDTO> albumDTOList) {
		this.albumDTOList = albumDTOList;
	}

	public List<AlbumDTO> getAlbums() {
		return albumDTOList;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getCoverPath() {
		return coverPath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.artistName);
		dest.writeTypedList(this.albumDTOList);
		dest.writeString(this.coverPath);
	}

	public ArtistDTO() {
	}

	protected ArtistDTO(Parcel in) {
		this.id = in.readString();
		this.artistName = in.readString();
		this.albumDTOList = new ArrayList<AlbumDTO>();
		in.readTypedList(this.albumDTOList, AlbumDTO.CREATOR);
		this.coverPath = in.readString();
	}

	public static final Parcelable.Creator<ArtistDTO> CREATOR = new Parcelable.Creator<ArtistDTO>() {
		public ArtistDTO createFromParcel(Parcel source) {
			return new ArtistDTO(source);
		}

		public ArtistDTO[] newArray(int size) {
			return new ArtistDTO[size];
		}
	};
}
