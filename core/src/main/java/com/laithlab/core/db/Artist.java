package com.laithlab.core.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Artist extends RealmObject {
	private String artistName;
	private String artistImageUrl;

	private RealmList<Album> albums;

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

	public void setAlbums(RealmList<Album> albums) {
		this.albums = albums;
	}

	public RealmList<Album> getAlbums() {
		return albums;
	}
}
