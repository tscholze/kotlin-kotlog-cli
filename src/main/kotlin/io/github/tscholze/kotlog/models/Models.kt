package io.github.tscholze.kotlog.models

import io.github.tscholze.kotlog.Kotlog
import kotlinx.serialization.Serializable
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Defines the blog layout
 *
 * @property titleText Title of the blog
 * @property styleName: Name of the applied CSS theme. Default: latex
 */
data class BlogConfiguration(
    val titleText: String,
    val footerText: String,
    val styleName: String = Kotlog.DEFAULT_STYLE_NAME
)

/**
 * Defines the post layout
 *
 * @property title Title of the blog post
 * @property innerHtml Rendered inner html content
*/
class PostConfiguration(
    val title: String,
    val innerHtml: String
) {
    companion object {
        /**
         * Creates a new configuration from given node.
         *
         * @param node Markdown DOM Node that contains required information
         * @return Created configuration object
         */
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

/**
 * Defines a content snippet
 *
 * @property title Title of the blog post
 * @property relativeUrl Url to the corresponding html page
 */
@Serializable
data class SnippetConfiguration(
    val title: String,
    val relativeUrl: String
)