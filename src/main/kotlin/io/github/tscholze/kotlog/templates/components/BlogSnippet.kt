package io.github.tscholze.kotlog.templates.components

import io.github.tscholze.kotlog.models.SnippetConfiguration
import io.github.tscholze.kotlog.templates.Renderable

class BlogSnippet(
    private val configuration: SnippetConfiguration
): Renderable {

    // MARK: - Renderable -

    override fun render(): String {

        var title = configuration.title
        if(title.length >= 75) {
            title = "${title.take(71)} ..."
        }

        return """
            <div class="snippet">
                <a href="${configuration.relativeUrl}">$title</a> <small color="#c0c0c0">(${configuration.published})</small>
            </div>
        """.trimIndent()
    }
}