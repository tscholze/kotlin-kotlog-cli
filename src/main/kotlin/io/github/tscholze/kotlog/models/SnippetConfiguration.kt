package io.github.tscholze.kotlog.models

import io.github.tscholze.kotlog.Kotlog.Companion.FILENAME_DATE_FORMATTER
import kotlinx.serialization.Serializable

/**
 * Defines a content snippet which will be used for generating
 * the Json feed.
 *
 * @property title Title of the post
 * @property primaryTag Primary (first) tag of the post
 * @property created: Markdown created timestamp
 * @property url Url to the post
 * @property coverImageUrl Url to the cover image
 */
@Serializable
class SnippetConfiguration(
    val title: String,
    val primaryTag: String,
    val created: String,
    val url: String,
    val coverImageUrl: String
) {
    companion object {
        /**
         * Creates a new instance from given arguments
         *
         * @param configuration Underlying post configuration
         * @param baseUrlString Blog's base url
         * @return Created snippet configuration
         */
        fun from(configuration: PostConfiguration, baseUrlString: String): SnippetConfiguration {
            return SnippetConfiguration(
                configuration.title,
                configuration.tags.first(),
                configuration.created.format(FILENAME_DATE_FORMATTER),
                "$baseUrlString/${configuration.filename}",
                "$baseUrlString/${configuration.filename}.png"
            )
        }
    }
}