package io.github.tscholze.kotlog.templates.html

import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.PostConfiguration
import io.github.tscholze.kotlog.templates.Renderable
import io.github.tscholze.kotlog.templates.components.RootContainer

class Post(
    private val configuration: BlogConfiguration,
    private val post: PostConfiguration
): Renderable {

    // MARK: - Renderable -

    override fun render(): String {
        val innerHtml =
            """
                <h1>${post.title}</h1>
                
                ${post.innerHtml}
                 
                 <a href="index.html">Back</a>
            """.trimIndent()

        return RootContainer(configuration, innerHtml).render()
    }
}