package com.laithlab.core.dto;


import android.os.Parcel;
import android.os.Parcelable;

public class SongDTO implements Parcelable {

	private String songTitle;
	private String songImageUrl;
	private long songDuration;
	private String songLocation;
	private String albumId;

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongImageUrl(String songImageUrl) {
		this.songImageUrl = songImageUrl;
	}

	public String getSongImageUrl() {
		return songImageUrl;
	}

	public void setSongDuration(long songDuration) {
		this.songDuration = songDuration;
	}

	public long getSongDuration() {
		return songDuration;
	}

	public void setSongLocation(String songLocation) {
		this.songLocation = songLocation;
	}

	public String getSongLocation() {
		return songLocation;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.songTitle);
		dest.writeString(this.songImageUrl);
		dest.writeLong(this.songDuration);
		dest.writeString(this.songLocation);
		dest.writeString(this.albumId);
	}

	public SongDTO() {
	}

	protected SongDTO(Parcel in) {
		this.songTitle = in.readString();
		this.songImageUrl = in.readString();
		this.songDuration = in.readLong();
		this.songLocation = in.readString();
		this.albumId = in.readString();
	}

	public static final Parcelable.Creator<SongDTO> CREATOR = new Parcelable.Creator<SongDTO>() {
		public SongDTO createFromParcel(Parcel source) {
			return new SongDTO(source);
		}

		public SongDTO[] newArray(int size) {
			return new SongDTO[size];
		}
	};
}
