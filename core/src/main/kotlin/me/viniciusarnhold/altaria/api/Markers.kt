package me.viniciusarnhold.altaria.api

import org.apache.logging.log4j.MarkerManager

/**
 * @author Vinicius Pegorini Arnhold.
 */
object Markers {

    private val BASE_ALTARIA_MARKER = MarkerManager.getMarker("BaseAltariaMarker")

    val DISPATCH = MarkerManager.getMarker("Dispatch").addParents(BASE_ALTARIA_MARKER)

}