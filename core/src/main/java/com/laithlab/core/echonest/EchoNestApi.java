package com.laithlab.core.echonest;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface EchoNestApi {

	@GET("/song/search")
	void getSong(
			@Query("artist") String artist,
			@Query("title") String title,
			Callback<EchoNestSearch> cb
	);
}
