package com.spawn.ai.interfaces;

import com.spawn.ai.model.BotResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IBotWebService {

    @GET("/message")
    Call<BotResponse> getBotResponse(@Query("q") String q);
}
