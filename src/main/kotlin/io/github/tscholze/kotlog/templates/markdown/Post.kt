package io.github.tscholze.kotlog.templates.markdown

import io.github.tscholze.kotlog.Kotlog.Companion.FILENAME_DATE_FORMATTER
import io.github.tscholze.kotlog.templates.Renderable
import java.util.*

class Post(
    private val title: String,
    private val innerHtml: String
):Renderable {
    override fun render(): String {

        val date = FILENAME_DATE_FORMATTER.format(Date().toInstant())

        return """
---
title: '$title'
date: '$date'
---

$innerHtml
""".trimIndent()
    }
}