package io.github.tscholze.kotlog.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * Defines the blog layout
 *
 * @property baseUrl Base url like 'https://tscholze.github.io/blog/'
 * @property titleText Title (Header) of the blog
 * @property footerText Footer text of the blog
 * @property outputDirectoryName The output were the generated content should be placed
 * @property youtubeKey YouTube API Key, default value is empty string.
 */
@Serializable
data class BlogConfiguration(
    @JsonNames("base_url")
    val baseUrl: String,


    val titleText: String,
    val footerText: String,
    val outputDirectoryName: String,
    val youtubeKey: String = "",
)