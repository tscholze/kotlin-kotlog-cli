package io.github.tscholze.kotlog.templates.components

import io.github.tscholze.kotlog.models.YouTubeComponentConfiguration
import io.github.tscholze.kotlog.templates.Renderable

class EmbeddedYouTubeVideo(
    private val video: YouTubeComponentConfiguration
): Renderable {

    // MARK: - Renderable -

    override fun render(): String {
       return """
            Yay, I just published [a new Video](${video.videoUrl}}) on my small [YouTube channel](https://www.youtube.com/user/TobiasScholze).

            <center>
                <iframe
                        width="560"
                        height="315"
                        src="${video.embedUrl}"
                        title="YouTube video player"
                        frameborder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen>
                </iframe>
            </center>

            Thanks for all your support, likes and comments. I try always to improve my content creations kills!
        """.trimIndent()
    }


}