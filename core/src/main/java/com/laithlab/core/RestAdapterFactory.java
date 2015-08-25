package com.laithlab.core;

import com.laithlab.core.echonest.EchoNestApi;
import com.laithlab.core.fma.FMAApi;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RestAdapterFactory {
	private static final String FMA_API_URL = "http://freemusicarchive.org/api";
	private static final String FMA_API_KEY = "1X1RMAS090YGVF7X";
	private static final String ECHO_NEST_API_URL = "http://developer.echonest.com/api/v4/";
	private static final String ECHO_NEST_API_KEY = "FHGW1IYODGDKLOXPR";


	public static FMAApi getFMAApi() {
		RestAdapter.Builder adapterBuilder = new RestAdapter.Builder()
				.setEndpoint(FMA_API_URL).setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestInterceptor.RequestFacade request) {
						request.addQueryParam("api_key", FMA_API_KEY);
						request.addQueryParam("format", "json");
					}
				});
		return adapterBuilder.build().create(FMAApi.class);
	}

	public static EchoNestApi getEchoNestApi(){

		RestAdapter.Builder adapterBuilder = new RestAdapter.Builder()
				.setEndpoint(ECHO_NEST_API_URL).setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestInterceptor.RequestFacade request) {
						request.addQueryParam("api_key", ECHO_NEST_API_KEY);
						request.addQueryParam("bucket", "tracks");
						request.addQueryParam("bucket", "id:7digital-US");
					}
				});
		return adapterBuilder.build().create(EchoNestApi.class);

	}

}
