package io.github.tscholze.kotlog.models

import kotlinx.serialization.Serializable

/**
 * Defines the blog layout
 *
 * @property baseUrl Base url like 'https://tscholze.github.io/blog/'
 * @property titleText Title (Header) of the blog
 * @property footerText Footer text of the blog
 * @property outputDirectoryName The output were the generated content should be placed
 * @property youtubeKey YouTube API Key, default value is empty string.
 * @property socialMedia List of social media platforms that the blog owner is using, default value is empty
 */
@Serializable
data class BlogConfiguration(
    val baseUrl: String,
    val titleText: String,
    val footerText: String,
    val outputDirectoryName: String,
    val youtubeKey: String = "",
    val socialMedia: List<SocialMedia> = emptyList()
)

/**
 * Describes a social media platform entry.
 *
 * @property platform Social media platform
 * @property id Your ID, username or other name
 * @property payload Optional payload used per platform
 */
@Serializable
data class SocialMedia(
    val platform: SocialMediaPlatform,
    val id: String,
    val payload: String? = null
)

/**
 * List of all supported and render-able social media
 * platforms.
 */
@Serializable
enum class SocialMediaPlatform {
    /**
     * twitter.com
     *
     * Use your handle with without the @ as id
     */
    TWITTER,

    /**
     * mastodon
     *
     * Use payload property to add your verification link
     */
    MASTODON,

    /**
     * github.com
     *
     * Use your username as id.
     */
    GITHUB
}

