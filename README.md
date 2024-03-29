# kotlin-kotlog-cli
**Kot**lin B**log** is a CLI static blog generator - not intended for public or production use. It features some handy shortcuts to streamline my content creation workflows. Kotlog is used to generate my personal blog: [tscholze.github.io/blog](https://tscholze.github.io/blog/)

## tl;dr
No, I do not think that you want to use this Kotlin CLI. It is meant as a content creation helper for me needs. But if you find it interesting, awesome!

![](https://github.com/tscholze/kotlin-kotlog-cli/blob/main/docs/kotlog-desc.png?raw=true)

## Why?
I was looking for a simple static site blog generator. Jekyll and others were too overpowered and too complex for my needs. Besides this, I wanted to have a more depth look into learning Kotlin and returning to JetBrains IDEs.

Besides this, I wanted to streamline my content creation workflows by letting the program do the heavy lifting in looking for information that is required for the resulting blog post.

## Status

|Service|Status|
|-|-|
|Qodana check|[![Qodana](https://github.com/tscholze/kotlin-kotlog-cli/actions/workflows/code_quality.yml/badge.svg)](https://github.com/tscholze/kotlin-kotlog-cli/actions/workflows/code_quality.yml)|
|Gradle build|[![Build](https://github.com/tscholze/kotlin-kotlog-cli/actions/workflows/build.yml/badge.svg)](https://github.com/tscholze/kotlin-kotlog-cli/actions/workflows/build.yml)|
## Structure
- `__posts/`  - contains all Markdown posts that will be rendered
- `__styles/` - contains all static style files that has to be embedded
- `www/` - contains generated content (configurable in Main.kt)

## Usage

Clone the [kotlog-template repository](https://github.com/tscholze/kotlin-kotlog-template) first, then you are ready to run the CLI.

```
java -jar kotlog.jar [options]
 -c 'My awesome title'  : Creates a new blog post
 -y 'beYqB6QXQuY'       : Creates a YouTube post
 -g                     : Generates HTML output
 -fg                    : To force generate output files"
 -p                     : Publish aka pushes changes to remote
 -co                    : Clears the output
``` 

## Configuration

### Using configuration instance
A Kotlog-generated blog ist configurated using the `BlogConfiguration` parameter of the `Kotlog` invocation.
See `Main.kt` for a working example:

```kotlin
fun main(args: Array<String>) {

    val configuration = BlogConfiguration(
        baseUrl = "https://tscholze.github.io/blog",
        titleText = "Tobias Scholze | The Stuttering Nerd",
        footerText = "Made with ❤️ without JavaScript| Kotlog | Tobias Scholze",
        outputDirectoryName = "www"
    )

    Kotlog(args, configuration)
}
```

### Using configuration file
Otherwise, Kotlog will try to load the configuration file from working directory's or user home's `~/.kotlog` file from the file system. Run `./kotlog` to create a new skeleton configuration file.

```json
{
    "baseUrl": "https://tscholze.github.io/blog",
    "titleText": "Tobias Scholze | The Stuttering Nerd",
    "footerText": "Made with ❤️ without JavaScript| Kotlog | Tobias Scholze",
    "outputDirectoryName": "www",
    "socialMedia": [
        {
            "platform": "GITHUB",
            "id": "tscholze"
        },
        {
            "platform": "TWITTER",
            "id": "tobonautilus"
        },
        {
            "platform": "MASTODON",
            "id": "@tobonaut@mastodon.social",
            "payload": "https://mastodon.social/@tobonaut"
        }
    ]
}
```

## How it looks
![Flow](https://github.com/tscholze/kotlin-kotlog-cli/blob/main/docs/kotlog-markdown2html.png?raw=true)
(Design, look and feel and other ui elements my differ from version to version)

## Features

### Blogging
- Creates pre-configuration Markdown files for blog posts 
- Supports Front-matter for meta information
- Renders markdown files into HTML blog articles
- Renders a feed.json with snippets of blog articles

### Content creation
- Shortcut for YouTube video announcements just with its video id

### Social media
 - Creates social media preview images for each blog post

## Ideas
- Switching to [Kobweb](http://kobweb.varabyte.com) / Compose for Web instead of HTML templates
- Embedding "auto share" of new blog posts

## Warning
Do not use this tool in production or something else besides education! I just started studying Kotlin and have no idea if the source generated or consumed by the CSS is GDPR or something else compliant.
If you have any other warnings for me, please open an issue. I'm here to learn!

## How to document
Kotlog uses [KDoc and Dokka](https://kotlinlang.org/docs/kotlin-doc.html) to document the Kotlin files it self. To run or update the documentation run one of the following Gradle commands:

* `dokkaGfm` - generate GH flavored markdown documentation  
* `dokkaHtml` - generate HTML documentation

After the task was successful, the generated documentation can be found in the `build/`folder.

## Thanks to
The app is built on the work of giants. Without the following folks, repositories and posts, my tiny project would not exist.

- [David Herman (bitspittle)](https://github.com/bitspittle) - one of the most aspiring Kotlin educator and developer of [Kobweb](http://kobweb.varabyte.com)
- [Marcel Reiter (MarcelReiter)](https://github.com/MarcelReiter) - an awesome college and for never being tired of my beginners questions
- [Liam Doherty (dohliam)](https://github.com/dohliam/dropin-minimal-css#theme-collections) for curating the drop-in css collection which is used as `style` in the app
- [Adrian Witaszak (adrianwitaszak)](https://github.com/adrianwitaszak) for fixing typos and proofreading the Readme

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
Dependencies or assets maybe licensed differently.
