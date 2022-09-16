package io.github.tscholze.kotlog.utils

/**
 * Returns a slugified version of the string.
 *
 * Based on:
 *  https://stackoverflow.com/questions/57298658/how-do-i-create-a-url-slug-extension
 */
fun String.toSlug() = lowercase()
    .replace("\n", " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")