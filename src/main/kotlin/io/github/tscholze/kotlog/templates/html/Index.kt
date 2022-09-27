package io.github.tscholze.kotlog.templates.html

import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.SnippetConfiguration
import io.github.tscholze.kotlog.templates.Renderable
import io.github.tscholze.kotlog.templates.components.BlogSnippet
import io.github.tscholze.kotlog.templates.components.RootContainer

class Index(
    private val blogConfiguration: BlogConfiguration,
    private val snippets: List<SnippetConfiguration>
): Renderable {

    // MARK: - Renderable -

    override fun render(): String {

        val sortedSnippets = snippets
            .sortedBy { it.published }
            .reversed()

        val posts = sortedSnippets
            .filter { it.primaryTag.lowercase() != "archive" }

        val archivedPosts = sortedSnippets
            .filter { it.primaryTag.lowercase() == "archive" }

        val innerHtml =
            """
              <h1>Blog</h1>
              ${posts.map { BlogSnippet(it).render() }.joinToString("~") { it }}
    
              <h3>Archive (from the far away past)</h3>
              ${archivedPosts.map { BlogSnippet(it).render() }.joinToString("~") { it }}
            """.trimIndent()

        // Render as page
        return RootContainer(blogConfiguration, innerHtml).render()
    }
}