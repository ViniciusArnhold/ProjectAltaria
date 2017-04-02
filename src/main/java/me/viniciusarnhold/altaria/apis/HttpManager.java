package me.viniciusarnhold.altaria.apis;

import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;


/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class HttpManager {

    @NotNull
    private static final OkHttpClient defaultHttpClient;

    @NotNull
    private static HttpManager ourInstance = new HttpManager();

    static {
        defaultHttpClient = new OkHttpClient.Builder().build();
    }

    private HttpManager() {
    }


    @NotNull
    public static HttpManager getInstance() {
        return ourInstance;
    }


    @NotNull
    public OkHttpClient getDefaultClient() {
        return defaultHttpClient;
    }
}
