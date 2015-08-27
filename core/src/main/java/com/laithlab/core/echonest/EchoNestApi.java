package com.laithlab.core.echonest;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EchoNestApi {

	@GET("/song/search")
	void getSongImage(
			@Query("artist") String artist,
			@Query("title") String title,
			Callback<EchoNestSearch> cb
	);

	@GET("/song/search")
	void getArtistImage(
			@Query("artist") String artist,
			Callback<EchoNestSearch> cb
	);
}
