package io.github.tscholze.kotlog.models

/**
 * Defines the blog layout
 *
 * @property baseUrl Base url like 'https://tscholze.github.io/blog/'
 * @property titleText Title (Header) of the blog
 * @property footerText Footer text of the blog
 * @property outputDirectoryName The output were the generated content should be placed
 */
data class BlogConfiguration(
    val baseUrl: String,
    val titleText: String,
    val footerText: String,
    val outputDirectoryName: String
)