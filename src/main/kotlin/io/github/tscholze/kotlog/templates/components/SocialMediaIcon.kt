package io.github.tscholze.kotlog.templates.components

import io.github.tscholze.kotlog.models.SocialMedia
import io.github.tscholze.kotlog.models.SocialMediaPlatform
import io.github.tscholze.kotlog.templates.Renderable

/**
 * Renders a linked social media icon.
 *
 * @param socialMedia Social media entry.
 */
class SocialMediaIcon(private val socialMedia: SocialMedia): Renderable {
    override fun render(): String {
        return when(socialMedia.platform) {
            SocialMediaPlatform.GITHUB -> "<a href=\"https://github.com/${socialMedia.id}\" title=\"GitHub\" target=\"_bank\"><i class=\"fab fa-github\"></i></a>"
            SocialMediaPlatform.MASTODON -> "<a rel=\"me\" href=\"${socialMedia.payload ?: ""}\" title=\"Mastodon\" target=\"_bank\"><i class=\"fab fa-mastodon\"></i></a>"
            SocialMediaPlatform.TWITTER -> "<a href=\"https://twitter.com/${socialMedia.id}\" title=\"Twitter\"  target=\"_bank\"><i class=\"fab fa-twitter\"></i></a>"
        }
    }
}

/**
 * Renders a space separated row of linked social media icons.
 *
 * @param socialMedias List of social media entries.
 */
class SocialMediaIcons(private val socialMedias: List<SocialMedia>): Renderable {
    override fun render(): String {
        return socialMedias
            .map { SocialMediaIcon(it).render()  }
            .joinToString(" ") { it }
    }
}