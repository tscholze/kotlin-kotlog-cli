package io.github.tscholze.kotlog.templates.components

import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.templates.Renderable

/**
 * Root template container renders a html page with given
 * inner content html value.
 *
 * @param configuration Blog configuration
 * @param contentHtml Content of container
 */
class RootContainer (
    private val configuration: BlogConfiguration,
    private val permaUrlPath: String,
    private val contentTitle: String? = null,
    private val contentAbstraction: String? = null,
    private val contentPreviewImageUrlString: String? = null,
    private val contentHtml: String
    ): Renderable {

    // MARK: - Renderable -

    override fun render(): String {
       return """<!doctype html>
        <html class="no-js" lang="">

        <head>
          <meta charset="utf-8">
          <meta name="description" content="">
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <meta property="og:type" content="article" />
          <meta name="twitter:card" content="summary_large_image">
          <meta property="og:url" content="${configuration.baseUrl}$permaUrlPath" />
          ${contentPreviewImageUrlString.let { "<meta property=\"og:image\" content=\"${configuration.baseUrl}$it\">" }}
          ${contentTitle.let { "<meta property=\"og:title\" content=\"${it}\">" }}
          ${contentAbstraction.let { "<meta property=\"og:description\" content=\"${it}\">" }}

          <title>${configuration.titleText}</title>

          <link rel="icon" href="favicon.ico" sizes="any">
          <link rel="icon" href="icon.svg" type="image/svg+xml">
          <link rel="apple-touch-icon" sizes="180x180" href="apple-touch-icon.png">

          <link rel="stylesheet" href="style.css">
        </head>

        <header>
            ${configuration.titleText}
            <hr />
        </header>

        <content>
            $contentHtml
        </content>

        <footer>
            <hr />
           ${configuration.footerText}
        </footer>

        </html>
    """.trimIndent()
    }
}