package me.viniciusarnhold.altaria.apis.imdb

import com.omertron.imdbapi.ImdbApi


/**
 * Created by Vinicius.

 * @since 1.0
 */
object Imdb {


    private val ourInstance = ImdbApi()


    fun api(): ImdbApi {
        return ourInstance
    }
}
