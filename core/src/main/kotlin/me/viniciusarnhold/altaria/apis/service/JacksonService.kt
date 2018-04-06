package me.viniciusarnhold.altaria.apis.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * @author Vinicius Pegorini Arnhold.
 */
class JacksonService {
    val mapper = ObjectMapper()
            .registerKotlinModule()
}