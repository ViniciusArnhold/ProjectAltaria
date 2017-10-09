package me.viniciusarnhold.altaria.apis.imdb

import com.omertron.imdbapi.ImdbApi


/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object Imdb {


    private val ourInstance = ImdbApi()


    fun api(): ImdbApi {
        return ourInstance
    }
}
