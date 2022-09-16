package io.github.tscholze.kotlog.models

import io.github.tscholze.kotlog.Kotlog
import kotlinx.serialization.Serializable
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.renderer.html.HtmlRenderer

data class BlogConfiguration(
    val title: String,
    val styleName: String = Kotlog.DEFAULT_STYLE_NAME,
    val alwaysClean: Boolean = false,
)

class PostConfiguration(
    val title: String,
    val innerHtml: String
) {
    companion object {
        fun fromNode(node: Node): PostConfiguration {
            // Check for front matter
            val frontMatterVisitor = YamlFrontMatterVisitor()
            node.accept(frontMatterVisitor)
            val title = frontMatterVisitor.data["title"]?.first() ?: Kotlog.MISSING_FRONT_MATTER_TITLE_WARNING

            // Generate inner html string
            val innerHtml = HtmlRenderer.builder().build().render(node)

            // Return created configuration
            return PostConfiguration(title, innerHtml)
        }
    }
}

@Serializable
data class SnippetConfiguration(
    val title: String,
    val relativeUrl: String
)

data class YouTubeComponentConfiguration(
    val title: String,
    val videoUrl: String,
    val embedUrl: String
)