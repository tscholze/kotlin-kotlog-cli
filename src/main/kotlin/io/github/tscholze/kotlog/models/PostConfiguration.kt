package io.github.tscholze.kotlog.models

import io.github.tscholze.kotlog.Kotlog
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
            val date = LocalDate.parse(dateString, Kotlog.FILENAME_DATE_FORMATTER)
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