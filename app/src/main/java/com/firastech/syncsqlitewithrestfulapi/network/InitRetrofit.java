package com.firastech.syncsqlitewithrestfulapi.network;

import com.firastech.syncsqlitewithrestfulapi.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public class InitRetrofit {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASEURL_DEVEM)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
