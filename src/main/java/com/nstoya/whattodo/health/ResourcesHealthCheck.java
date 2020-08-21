package com.nstoya.whattodo.health;

import com.codahale.metrics.health.HealthCheck;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class ResourcesHealthCheck extends HealthCheck {

    OkHttpClient client;
    private final HttpUrl path;
    private final String authToken;

    public ResourcesHealthCheck(String path, String authToken) {
        this.client = new OkHttpClient();
        this.path = HttpUrl.parse(path);
        this.authToken = authToken;
    }

    //TODO check resources
    @Override
    protected Result check() throws Exception {
        Request request = new Request.Builder()
                .url(path)
                .get()
                .addHeader("Authorization", authToken)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            return Result.healthy();
        }
        return Result.unhealthy("code: " + response.code(), response.body() != null ? response.body().string(): null);
    }
}
