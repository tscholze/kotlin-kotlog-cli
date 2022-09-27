package io.github.tscholze.kotlog.templates

import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.templates.components.RootContainer

class Index(
    val blogConfiguration: BlogConfiguration,
    val content: String,
    val archivedContent: String
): HtmlGeneratable {

    // MARK: - HtmlGeneratable -

    override fun render(): String {
        val innerHtml =
            """
              <h1>Blog</h1>
              $content
    
              <h3>Archive (from the far away past)</h3>
              $archivedContent
            """.trimIndent()

        // Render as page
        return RootContainer(blogConfiguration, innerHtml).render()
    }
}