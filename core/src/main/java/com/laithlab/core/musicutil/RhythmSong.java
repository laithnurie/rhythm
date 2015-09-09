package com.laithlab.core.musicutil;

public class RhythmSong {

	final private String artistTitle;
	final private String albumTitle;
	final private String trackTitle;
	final private byte[] imageData;
	final private float duration;
	final private String songLocation;

	private RhythmSong(RhythmSongBuilder builder){
		this.artistTitle = builder.artistTitle;
		this.albumTitle = builder.albumTitle;
		this.trackTitle = builder.trackTitle;
		this.imageData = builder.imageData;
		this.duration = builder.duration;
		this.songLocation = builder.songLocation;
	}

	public String getArtistTitle() {
		return artistTitle;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public float getDuration() {
		return duration;
	}

	public String getSongLocation() {
		return songLocation;
	}

	public static class RhythmSongBuilder {
		private String artistTitle;
		private String albumTitle;
		private String trackTitle;
		private byte[] imageData;
		private float duration;
		private String songLocation;

		public RhythmSongBuilder artistTitle(String artistTitle){
			this.artistTitle = artistTitle;
			return this;
		}

		public RhythmSongBuilder albumTitle(String albumTitle){
			this.albumTitle = albumTitle;
			return this;
		}

		public RhythmSongBuilder trackTitle(String trackTitle){
			this.trackTitle = trackTitle;
			return this;
		}

		public RhythmSongBuilder imageData(byte[] imageData){
			this.imageData = imageData;
			return this;
		}

		public RhythmSongBuilder duration(float duration){
			this.duration = duration;
			return this;
		}

		public RhythmSongBuilder songLocation(String songLocation){
			this.songLocation = songLocation;
			return this;
		}

		public RhythmSong build(){
			return new RhythmSong(this);
		}
	}

}


