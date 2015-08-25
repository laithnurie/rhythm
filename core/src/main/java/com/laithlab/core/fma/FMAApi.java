package com.laithlab.core.fma;

import retrofit.Callback;
import retrofit.http.GET;

public interface FMAApi {

	@GET("/get/curators.json")
	void getCurators(
			Callback<Curator> cb
	);
}