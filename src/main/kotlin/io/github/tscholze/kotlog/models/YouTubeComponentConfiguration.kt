package io.github.tscholze.kotlog.models

/**
 * Defines a YouTube component.
 *
 * @property title Title of the blog post
 * @property videoUrl Url to the YouTube browser video
 * @property embedUrl URl to the embedded YouTube player
 */
data class YouTubeComponentConfiguration(
    val title: String,
    val videoUrl: String,
    val embedUrl: String
)