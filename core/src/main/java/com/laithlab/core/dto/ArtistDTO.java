package com.laithlab.core.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ArtistDTO implements Parcelable {

	private String artistName;
	private String artistImageUrl;
	private List<AlbumDTO> albumDTOList;


	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistImageUrl(String artistImageUrl) {
		this.artistImageUrl = artistImageUrl;
	}

	public String getArtistImageUrl() {
		return artistImageUrl;
	}

	public void setAlbums(List<AlbumDTO> albumDTOList) {
		this.albumDTOList = albumDTOList;
	}

	public List<AlbumDTO> getAlbums() {
		return albumDTOList;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.artistName);
		dest.writeString(this.artistImageUrl);
		dest.writeTypedList(this.albumDTOList);
	}

	public ArtistDTO() {
	}

	protected ArtistDTO(Parcel in) {
		this.artistName = in.readString();
		this.artistImageUrl = in.readString();
		this.albumDTOList = new ArrayList<AlbumDTO>();
		in.readTypedList(this.albumDTOList, AlbumDTO.CREATOR);
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
