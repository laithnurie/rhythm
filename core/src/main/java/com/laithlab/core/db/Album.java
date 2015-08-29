package com.laithlab.core.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Album extends RealmObject {
	private String albumTitle;
	private String albumImageUrl;

	private RealmList<Song> songs;


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

	public void setSongs(RealmList<Song> songs) {
		this.songs = songs;
	}

	public RealmList<Song> getSongs() {
		return songs;
	}
}
