package io.github.tscholze.kotlog

import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.PostConfiguration
import io.github.tscholze.kotlog.models.SnippetConfiguration
import io.github.tscholze.kotlog.models.YouTubeComponentConfiguration
import io.github.tscholze.kotlog.utils.toSlug
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText

/**
 * Kotlog represents a static side generator which is tailored to the use case
 * of the developer behind this spare time project.
 *
 * @param args CLI arguments which will be processed.
 *
 * Possible CLI arguments:
 *  - `-c 'My awesome title'`: Creates a new blog post
 *  - `-y beYqB6QXQuY`: Creates a YouTube post
 *  - `-g`: Generates html output
 *  - `-p`: Publish aka pushes changes to remote
 */
class Kotlog(args: Array<String>) {
    companion object {
        // MARK: - Constants -

        const val RELATIVE_POSTS_PATH = "__posts"
        const val RELATIVE_STYLES_PATH = "__styles"
        const val RELATIVE_OUTPUT_PATH = "__output"
        const val RELATIVE_TEMPLATES_PATH = "__templates"

        const val DEFAULT_STYLE_NAME = "latex"
        const val DEFAULT_STYLE_OUTPUT_FILENAME = "style.css"

        const val DEFAULT_POST_TEMPLATE_NAME = "post.html"
        const val DEFAULT_INDEX_TEMPLATE_NAME = "index.html"
        const val DEFAULT_SNIPPET_TEMPLATE_NAME = "snippet.html"
        const val DEFAULT_COMPONENT_YOUTUBE_VIDEO_TEMPLATE_NAME = "component_youtube_content.html"

        const val DEFAULT_MARKDOWN_POST_TEMPLATE_NAME = "post.md"

        const val DEFAULT_JSON_OUTPUT_FILENAME = "posts.json"
        const val DEFAULT_INDEX_OUTPUT_FILENAME = "index.html"

        const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"

        const val MISSING_TEMPLATE_WARNING = "Template not found."
        const val MISSING_FRONT_MATTER_TITLE_WARNING = "MISSING_TEMPLATE_WARNING"

        // MARK: - Properties -

        val parser: Parser
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

    // MARK: - Init -

    init {
        // Perform sanity check.
        //
        // If successful
        //  -> process arguments
        // Else
        // -> print error message and exit
        if(sanityCheckWithTryRepair()) {
            processCliArguments(args)
        } else {
            printSanityCheckFailedMessage()
        }
    }

    // MARK: - CLI -

    private fun processCliArguments(args: Array<String>) {
        val parser = ArgParser("Kotlog - Blog generator")

        val title by parser.option(
            ArgType.String,
            shortName = "c",
            description = "Create new post with given title"
        )

        val youtubeId by parser.option(
            ArgType.String,
            shortName = "y",
            description = "Creates a new post for given YT video id"
        )

        val generate by parser.option(
            ArgType.Boolean, shortName = "g",
            description = "Generate blog content"
        )

        val publish by parser.option(
            ArgType.Boolean, shortName = "p",
            description = "Publishes the current state of the blog"
        )

        parser.parse(args)

        // Evaluate CLI arguments
        when {
            // Check if a title is set -> create new post
            title != null && title?.isNotBlank() == true -> {
                generateMarkdownPost(title!!)
            }

            // Checks if -y is set -> Create new YouTube post
            youtubeId != null && youtubeId?.isNotBlank() == true -> {
                generateMarkdownYoutubePost(youtubeId!!)
            }

            // Check if -g is set -> Generate html
            generate == true -> {
                generate(BlogConfiguration("Tobias Scholze | The Stuttering Nerd"))
            }

            // Check if -p is set -> Publish html
            publish == true -> {
                pushToRemote()
            }
        }
    }

    private fun processCliOpenMarkdownFile(filename: String) {
        print("Open Markdown file? (y/n)\n> ")
        val boolString = readLine()

        if (boolString == "y") {
            Runtime.getRuntime().exec("code $RELATIVE_POSTS_PATH/$filename")
        }
    }

    private fun processCliPublishOutput() {
        print("Do you want to publish the changes? (y/n)\n> ")
        val boolString = readLine()

        if (boolString == "y") {
            pushToRemote()
        }
    }

    // MARK: - Generators -

    private fun generate(configuration: BlogConfiguration) {
        // 1. Clean existing output if needed.
        if (configuration.alwaysClean) {
            cleanOutput()
        }

        // 2. Generate posts
        generateHtmlPosts()

        // 3. Generate landing page
        generateIndex(configuration)

        // 4. Copy style
        generateHtmlStyle(configuration.styleName)

        // 5. Generate Json
        generateJson()

        // 6. Print command to open output
        printOutputFilePath()

        // 7. Ask the user if changes should be published
        processCliPublishOutput()
    }

    private fun generateMarkdownPost(title: String, content: String? = null) {
        val template = readFromTemplates(DEFAULT_MARKDOWN_POST_TEMPLATE_NAME)
        val dateString = SimpleDateFormat(DEFAULT_DATE_PATTERN).format(Date())
        val filename = "$dateString-${title.toSlug()}.md"
        val markdown = template
            .replace("{{title}}", title)
            .replace("{{date}}", dateString)
            .replace("{{content}}", content ?: "Your content")

        writeToPosts(filename, markdown)
        printNewPostMessage(filename)
        processCliOpenMarkdownFile(filename)
    }

    private fun generateMarkdownYoutubePost(videoId: String) {
        val videoUrl = "https://www.youtube.com/watch?v=$videoId"
        val embedUrl = "https://www.youtube.com/embed/$videoId"
        val title = Jsoup.parse(URL(videoUrl), 3000).title()
        val configuration = YouTubeComponentConfiguration(title, videoUrl, embedUrl)
        val content = inflateComponentYouTubeContent(configuration)

        generateMarkdownPost(title, content)
    }

    private fun generateHtmlPosts() {
        File(RELATIVE_POSTS_PATH)
            .walk()
            .filter { it.extension == "md" }
            .forEach {
                val configuration = PostConfiguration.fromNode(parser.parse(it.readText()))
                val content = inflatePostTemplate(configuration)
                val filename = "${it.nameWithoutExtension}.html"
                writeToOutput(filename, content)
            }
    }

    private fun generateIndex(blogConfiguration: BlogConfiguration) {
        // Create index file
        val content = readSnippetConfigurations()
            .joinToString("~") { inflateSnippetTemplate(it) }

        val html = readFromTemplates(DEFAULT_INDEX_TEMPLATE_NAME)
            .replace("{{title}}", blogConfiguration.title)
            .replace("{{content}}", content)

        // Write file
        writeToOutput(DEFAULT_INDEX_OUTPUT_FILENAME, html)
    }

    private fun generateJson() {
        val json = Json.encodeToString(readSnippetConfigurations())
        writeToOutput(DEFAULT_JSON_OUTPUT_FILENAME, json)
    }

    private fun generateHtmlStyle(styleName: String) {
        val file = File("$RELATIVE_STYLES_PATH/$styleName.css")
        if (!file.exists()) return
        val style = file.readText()
        writeToOutput(DEFAULT_STYLE_OUTPUT_FILENAME, style)
    }

    // MARK: - Pretty Prints -

    private fun printOutputFilePath() {
        println("")
        println("Html has been generated to folder:")
        println("Location: ${Paths.get("").toAbsolutePath()}/$RELATIVE_OUTPUT_PATH/")
        println("")
    }

    private fun printNewPostMessage(filenameWithExtension: String) {
        println("")
        println("New post '$filenameWithExtension' has been created!")
        println("Location: ${Paths.get("").toAbsolutePath()}/$RELATIVE_POSTS_PATH/$filenameWithExtension")
        println("Run `kotlog -g` to generate the html.")
        println("")
    }

    private fun printSanityCheckFailedMessage() {
        println("")
        println("Error!")
        println("One ore more folders with required content are missing.")
        println("Please check the template and style folder for its existence and content.")
        println("")
    }

    // MARK: - Cleaning helper -

    private fun cleanOutput() {
        File(RELATIVE_OUTPUT_PATH)
            .walk()
            .forEach { it.delete() }
    }

    // MARK: - Inflaters -

    private fun inflateSnippetTemplate(configuration: SnippetConfiguration): String {
        return readFromTemplates(DEFAULT_SNIPPET_TEMPLATE_NAME)
            .replace("{{title}}", configuration.title)
            .replace("{{relative_url}}", configuration.relativeUrl)
    }

    private fun inflatePostTemplate(configuration: PostConfiguration): String {
        return readFromTemplates(DEFAULT_POST_TEMPLATE_NAME)
            .replace("{{title}}", configuration.title)
            .replace("{{content}}", configuration.innerHtml)
    }

    private fun inflateComponentYouTubeContent(configuration: YouTubeComponentConfiguration): String {
        return readFromTemplates(DEFAULT_COMPONENT_YOUTUBE_VIDEO_TEMPLATE_NAME)
            .replace("{{title}}", configuration.title)
            .replace("{{youtube_url}}", configuration.videoUrl)
            .replace("{{embed_url}}", configuration.embedUrl)
    }

    // MARK: - File access -

    private fun readSnippetConfigurations(): List<SnippetConfiguration> {
        return File(RELATIVE_OUTPUT_PATH)
            .walk()
            .filter { it.extension == "html" }
            .map { SnippetConfiguration(Jsoup.parse(it.readText()).title(), it.name) }
            .toList()
    }

    private fun readFromTemplates(filename: String): String {
        val file = File("$RELATIVE_TEMPLATES_PATH/$filename")
        if (!file.exists()) return MISSING_TEMPLATE_WARNING
        return file.readText()
    }

    private fun writeToPosts(filenameWithExtension: String, input: String) {
        writeToPath("$RELATIVE_POSTS_PATH/$filenameWithExtension", input)
    }

    private fun writeToOutput(filenameWithExtension: String, input: String) {
        writeToPath("$RELATIVE_OUTPUT_PATH/$filenameWithExtension", input)
    }

    private fun writeToPath(filePath: String, input: String) {
        // If file exists, overwrite it
        // This will not happen if you clean the output folder first
        val path = Path(filePath)
        if (path.exists()) {
            path.writeText(input)
        } else {
            // 1. check if out directory exists
            val dirPath = Path(filePath.split("/").dropLast(1).joinToString("/"))
            if (!dirPath.exists()) {
                dirPath.createDirectory()
            }

            // 2. Create new html file
            val newFile = path.toFile()
            newFile.createNewFile()
            newFile.writeText(input)
        }
    }

    // MARK: - Git -

    private fun pushToRemote() {
        val message = "Content update ${SimpleDateFormat(DEFAULT_DATE_PATTERN).format(Date())}"
        val cmd1 = "git commit -am '$message'"
        Runtime.getRuntime().exec("git add ${Paths.get("").toAbsolutePath()}/$RELATIVE_OUTPUT_PATH/*")
        Runtime.getRuntime().exec("git add ${Paths.get("").toAbsolutePath()}/$RELATIVE_POSTS_PATH/*")
        Runtime.getRuntime().exec("git commit -am '$message'")
        Runtime.getRuntime().exec("git push origin main")
    }

    // MARK: - Sanity checks -

    private fun sanityCheckWithTryRepair(): Boolean {
        var isValid = true

        // Check if folders that must have files in it exists.
        val requiredContentInFolders =
            listOf(RELATIVE_TEMPLATES_PATH, RELATIVE_STYLES_PATH)
                .forEach {
                    val file = File(it)
                    if (!file.exists() || !file.isDirectory || file.listFiles().isEmpty()) {
                        isValid = false
                    }
                }

        return isValid
    }
}
