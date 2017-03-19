package me.viniciusarnhold.altaria.apis;

import okhttp3.OkHttpClient;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class HttpManager {
    private static final OkHttpClient defaultHttpClient;
    private static HttpManager ourInstance = new HttpManager();

    static {
        defaultHttpClient = new OkHttpClient.Builder().build();
    }

    private HttpManager() {
    }

    public static HttpManager getInstance() {
        return ourInstance;
    }

    public OkHttpClient getDefaultClient() {
        return defaultHttpClient;
    }
}
