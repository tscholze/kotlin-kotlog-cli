package io.github.tscholze.kotlog.templates

/**
 * Defines a render-able html object
 */
interface Renderable {
    /**
     * Renders the object as html.
     */
    fun render(): String
}