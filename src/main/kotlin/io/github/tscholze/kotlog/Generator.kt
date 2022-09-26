package io.github.tscholze.kotlog

import com.lordcodes.turtle.shellRun
import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.PostConfiguration
import io.github.tscholze.kotlog.models.SnippetConfiguration
import io.github.tscholze.kotlog.models.YouTubeComponentConfiguration
import io.github.tscholze.kotlog.utils.toSlug
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
 * @param args CLI arguments which will be processed
 * @param configuration Blog configuration
 *
 * Possible CLI arguments:
 *  - `-c 'My awesome title'`: Creates a new blog post
 *  - `-y beYqB6QXQuY`: Creates a YouTube post
 *  - `-g`: Generates html output
 *  - `-p`: Publish aka pushes changes to remote
 */
class Kotlog(args: Array<String>, configuration: BlogConfiguration) {
    companion object {
        // MARK: - Internal constants -

        internal const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
        internal const val MISSING_TEMPLATE_WARNING = "Template not found."
        internal const val MISSING_FRONT_MATTER_TITLE_WARNING = "MISSING_TEMPLATE_WARNING"

        // MARK: - Private constants -

        private const val RELATIVE_POSTS_PATH = "__posts"
        private const val RELATIVE_STYLES_PATH = "__styles"
        private const val RELATIVE_OUTPUT_PATH = "__output"
        private const val RELATIVE_TEMPLATES_PATH = "__templates"
        private const val DEFAULT_POST_TEMPLATE_NAME = "post.html"
        private const val DEFAULT_INDEX_TEMPLATE_NAME = "index.html"
        private const val DEFAULT_SNIPPET_TEMPLATE_NAME = "snippet.html"
        private const val DEFAULT_COMPONENT_YOUTUBE_VIDEO_TEMPLATE_NAME = "component_youtube_content.html"
        private const val DEFAULT_MARKDOWN_POST_TEMPLATE_NAME = "post.md"
        private const val DEFAULT_JSON_OUTPUT_FILENAME = "posts.json"
        private const val DEFAULT_INDEX_OUTPUT_FILENAME = "index.html"
        private val EMBEDDED_FILENAMES = listOf("style.css","apple-touch-icon.png","favicon.ico", "icon.svg")
    }

    // MARK: - Private properties -

    private val configuration: BlogConfiguration

    // MARK: - Init -

    init {
        // Set configuration
        this.configuration = configuration

        // Run generator
        run(args)
    }

    // MARK: - Run -

    private fun run(args: Array<String>) {
        // Greet the user
        printGreeting()

        // 1. Ensure at least one cli argument is given
        //      -> if not, print help message
        // 2. Perform sanity check.
        //      -> if not, print error message
        // 3. Process cli arguments
        if(args.isEmpty()) {
            printHelp()
        }
        else if (sanityCheckRequiredFoldersAndFiles()) {
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
            ArgType.Boolean,
            shortName = "g",
            description = "Generate blog content"
        )

        val publish by parser.option(
            ArgType.Boolean,
            shortName = "p",
            description = "Publishes the current state of the blog"
        )

        val clean by parser.option(
            ArgType.Boolean, shortName = "co",
            description = "Cleans the output directory"
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
                generateHtmlOutput()
            }

            // Check if -p is set -> Publish html
            publish == true -> {
                pushToRemote()
            }

            // Check if -co is set -> Clean output
            clean == true -> {
                cleanOutput()
            }
        }
    }

    private fun processCliOpenMarkdownFile(filename: String) {
        print("Open Markdown file in VSCode? (y/n)\n> ")
        val boolString = readLine()

        if (boolString == "y") {
            shellRun {
                val path = "${Paths.get("").toAbsolutePath()}/$RELATIVE_POSTS_PATH/$filename"
                command("code", listOf(path))
            }
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

    private fun generateMarkdownPost(title: String, content: String? = null) {
        val template = readFromTemplates(DEFAULT_MARKDOWN_POST_TEMPLATE_NAME)
        val dateString = SimpleDateFormat(DEFAULT_DATE_PATTERN).format(Date())
        val filename = "$dateString-${title.toSlug()}.md"
        val markdown = template
            .replace("{{title}}", title)
            .replace("{{date}}", dateString)
            .replace("{{content}}", content ?: "Your content goes here")

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

    private fun generateHtmlOutput() {
        // 1. Get configurations from markdown files
        val configurations = File(RELATIVE_POSTS_PATH)
            .walk()
            .filter { it.extension == "md" }
            .map { PostConfiguration.fromFile(it) }

        val snippets = configurations
            .map { SnippetConfiguration.from(it) }
            .toList()

        // 2. Transform configurations to html posts
        configurations
            .forEach { writeToOutput(it.filename, inflatePostTemplate(it)) }

        // 3. Transform configurations to html index
        generateIndex(snippets)

        // 4. Copy styles
        embedStyling()

        // 5. Transform configurations to feed
        generateJsonFeed(snippets)

        // 6. Print command to open output
        printOutputFilePath()

        // 7. Ask the user if changes should be published
        processCliPublishOutput()
    }

    private fun generateIndex(configurations: List<SnippetConfiguration>) {
        // Create index file
        val content = configurations
            .filter { it.primaryTag.lowercase() != "archive" }
            .sortedBy { it.relativeUrl }
            .reversed()
            .joinToString("~") { inflateSnippetTemplate(it) }

        val archivedContent = configurations
            .filter { it.primaryTag.lowercase() == "archive" }
            .sortedBy { it.relativeUrl }
            .reversed()
            .joinToString("~") { inflateSnippetTemplate(it) }

        val html = readFromTemplates(DEFAULT_INDEX_TEMPLATE_NAME)
            .replace("{{title}}", configuration.titleText)
            .replace("{{content}}", content)
            .replace("{{archived_content}}", archivedContent)

        // Write file
        writeToOutput(DEFAULT_INDEX_OUTPUT_FILENAME, html)
    }

    private fun generateJsonFeed(configurations: List<SnippetConfiguration>) {
        val json = Json.encodeToString(configurations)
        writeToOutput(DEFAULT_JSON_OUTPUT_FILENAME, json)
    }

    private fun embedStyling() {
        EMBEDDED_FILENAMES
            .forEach { copyFile("$RELATIVE_STYLES_PATH/$it", RELATIVE_OUTPUT_PATH) }
    }

    // MARK: - Pretty Prints -

    private fun printGreeting() {
        println("")
        println("Welcome to Kotlog <3")
        println("")
    }

    private fun printHelp() {
        println("No arguments given.")
        println("Please specify an argument what you want to do.")
        println("    -c 'My awesome title' : To create a new Markdown post")
        println("    -y 'xcg24fa' : To create a new YouTube video Markdown post")
        println("    -g : To generate output files")
        println("    -p : To publish output files")
    }

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
            .filter { it.nameWithoutExtension != "assets" }
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

    private fun copyFile(fromFilePath: String, toFilePath: String) {
        shellRun {
            command("cp", listOf("-f", fromFilePath, toFilePath))
        }
    }

    // MARK: - Git -

    private fun pushToRemote() {
        val message = "Content update ${SimpleDateFormat(DEFAULT_DATE_PATTERN).format(Date())}"
        shellRun {
            git.commitAllChanges(message)
            git.push("origin", "main")
            git.currentBranch()
        }
    }

    // MARK: - Sanity checks -

    private fun sanityCheckRequiredFoldersAndFiles(): Boolean {
        var isValid = true

        // Check if folders that must have files in it exists.
        listOf(RELATIVE_TEMPLATES_PATH, RELATIVE_STYLES_PATH)
            .forEach {
                val file = File(it)
                if (!file.exists() || !file.isDirectory || file.listFiles().isEmpty()) {
                    isValid = false
                }
            }

        // Add other checks if needed ...

        return isValid
    }
}