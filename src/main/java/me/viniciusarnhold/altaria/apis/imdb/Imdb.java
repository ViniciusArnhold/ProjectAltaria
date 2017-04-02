package me.viniciusarnhold.altaria.apis.imdb;

import com.omertron.imdbapi.ImdbApi;
import org.jetbrains.annotations.NotNull;


/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Imdb {


    @NotNull
    private static ImdbApi ourInstance = new ImdbApi();

    private Imdb() {
    }


    @NotNull
    public static ImdbApi api() {
        return ourInstance;
    }
}
