package com.spawn.ai.network;

import android.content.Context;

import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.interfaces.IBotWebService;
import com.spawn.ai.model.BotResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceUtils {

    private static WebServiceUtils webServiceUtils;
    private Retrofit retrofit;
    private static final String BOT_URL = "https://api.wit.ai";
    private static IBotObserver iBotObserver;
    String token;

    public static WebServiceUtils getInstance(Context context) {
        if (webServiceUtils == null) {
            webServiceUtils = new WebServiceUtils();
        }
        return webServiceUtils;
    }

    public void setUpObserver(Context context) {
        this.iBotObserver = (IBotObserver) context;
    }

    public Retrofit getRetrofitClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new BotInterceptor(token))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void getBotResponse(String q) {

        if (retrofit != null) {
            callWebservice(q);

        } else {
            retrofit = getRetrofitClient();
            callWebservice(q);
        }
    }

    public void callWebservice(String q) {
        iBotObserver.loading();
        final IBotWebService iBotWebService = retrofit.create(IBotWebService.class);
        final Call<BotResponse> botIntentsCall = iBotWebService.getBotResponse(q);
        botIntentsCall.enqueue(new Callback<BotResponse>() {
            @Override
            public void onResponse(Call<BotResponse> call, Response<BotResponse> response) {
                if (response.isSuccessful()) {
                    BotResponse botResponse = response.body();
                    iBotObserver.notifyBotResponse(botResponse);
                } else {
                    iBotObserver.notifyBotError();
                }
            }

            @Override
            public void onFailure(Call<BotResponse> call, Throwable t) {
                iBotObserver.notifyBotError();
            }
        });
    }
}
