package io.github.tscholze.kotlog.models

import io.github.tscholze.kotlog.Kotlog.Companion.DATE_FORMATTER
import kotlinx.serialization.Serializable
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.time.LocalDate

/**
 * Defines the blog layout
 *
 * @property baseUrlString Base url like 'https://tscholze.github.io/blog/'
 * @property titleText Title (Header) of the blog
 * @property footerText Footer text of the blog
 * @property outputDirectoryName The output were the generated content should be placed
 */
data class BlogConfiguration(
    val baseUrlString: String,
    val titleText: String,
    val footerText: String,
    val outputDirectoryName: String
)

/**
 * Defines the post layout
 *
 * @property title Title of the blog post
 * @property innerHtml Rendered inner html content
 * @property abstract: Abstract text of the content
 * @property filename: Post's html file name
 * @property created: Markdown created timestamp
 * @property tags: Assigns tag list
 * @property innerHtml: Inner html (content) value
 */
class PostConfiguration(
    val title: String,
    val abstract: String,
    val filename: String,
    val created: LocalDate,
    val tags: List<String>,
    val innerHtml: String,
) {
    companion object {

        // MARK: - Factories -

        /**
         * Creates a new configuration from given file.
         *
         * @param file Markdown file that contains required information
         * @return Created configuration object
         */
        fun fromFile(file: File): PostConfiguration {

            // Parse file
            val node = parser.parse(file.readText())

            // Check for front matter data
            val frontMatterVisitor = YamlFrontMatterVisitor()
            node.accept(frontMatterVisitor)

            // Ensure a title is given
            val title = frontMatterVisitor.data["title"]?.first()
            if (title.isNullOrEmpty()) {
                throw AssertionError(">title< missing in front matter of post: '$title'")
            }

            // Ensure a date is given
            val dateString = frontMatterVisitor.data["date"]?.first()
            if (dateString.isNullOrEmpty()) {
                throw AssertionError(">date< missing in front matter of post: '$title'")
            }

            val abstract = frontMatterVisitor.data["abstract"]?.first() ?: ""
            val date = LocalDate.parse(dateString, DATE_FORMATTER)
            val tags = frontMatterVisitor.data["tags"] ?: listOf("none")

            // Generate inner html string
            val innerHtml = HtmlRenderer.builder().build().render(node)

            // Generate filename
            val filename = "${file.nameWithoutExtension}.html"

            // Return created configuration
            return PostConfiguration(
                title,
                abstract,
                filename,
                date,
                tags,
                innerHtml,
            )
        }

        // MARK: - Private properties -

        private val parser: Parser
            get() {
                val extensions = mutableListOf<Extension>()
                extensions.add(AutolinkExtension.create())
                extensions.add(YamlFrontMatterExtension.create())
                extensions.add(TablesExtension.create())

                // Build parser
                return Parser.builder()
                    .extensions(extensions)
                    .build()
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
 * @property title Title of the post
 * @property relativeUrl Url to the corresponding html page
 * @property primaryTag Primary (first) tag of the post
 * @property created: Markdown created timestamp
 * @property urlString Url to the post
 * @property coverImageUrlString Url to the cover image
 */
@Serializable
class SnippetConfiguration(
    val title: String,
    val relativeUrl: String,
    val primaryTag: String,
    val created: String,
    private val urlString: String,
    private val coverImageUrlString: String
) {
    companion object {
        fun from(configuration: PostConfiguration, baseUrlString: String): SnippetConfiguration {
            return SnippetConfiguration(
                configuration.title,
                configuration.filename,
                configuration.tags.first(),
                configuration.created.format(DATE_FORMATTER),
                "$baseUrlString/{${configuration.filename}",
                "$baseUrlString/{${configuration.filename}.png"
            )
        }
    }
}