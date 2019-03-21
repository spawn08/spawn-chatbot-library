package com.spawn.ai.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BotInterceptor implements Interceptor {

    private String token;

    public BotInterceptor(String token) {
        this.token = token;

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authentication = request
                .newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authentication);
    }
}
