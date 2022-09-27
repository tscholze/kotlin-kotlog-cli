package io.github.tscholze.kotlog.templates

/**
 * Defines a render-able html object
 */
interface HtmlGeneratable {
    /**
     * Renders the object as html.
     */
    fun render(): String
}