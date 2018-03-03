package com.firastech.syncsqlitewithrestfulapi.network;

import com.firastech.syncsqlitewithrestfulapi.model.DataResponse;
import com.firastech.syncsqlitewithrestfulapi.model.SendResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("send.php")
    Call<SendResponse> sendName(@Field("name") String name);

    @GET("get.php")
    Call<DataResponse> getName();
}
