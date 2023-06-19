package io.github.tscholze.kotlog.templates.json

import io.github.tscholze.kotlog.templates.Renderable

class ConfigurationHomeFile: Renderable {
    override fun render(): String {
        return """
            {
                "baseUrl": "https://to-the-blog.tld",
                "titleText": "Your name | tag line",
                "footerText": "Made with ❤️ without JavaScript| Kotlog",
                "outputDirectoryName": "docs",
                "socialMedia": [
                    {
                        "platform": "GITHUB",
                        "id": "your_handle"
                    },
                    {
                        "platform": "TWITTER",
                        "id": "your_handle"
                    },
                    {
                        "platform": "MASTODON",
                        "id": "your_handle",
                        "payload": "https://mastodon.social/@your_handle"
                    }
                ]
            }
        """.trimIndent()
    }
}