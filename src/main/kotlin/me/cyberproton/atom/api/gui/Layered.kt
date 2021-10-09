package me.cyberproton.atom.api.gui

interface Layered {
    fun getLayer(layerId: String): Layer?

    fun addLayer(layer: Layer)

    fun removeLayer(layerId: String)

    fun removeLayer(layer: Layer)
}