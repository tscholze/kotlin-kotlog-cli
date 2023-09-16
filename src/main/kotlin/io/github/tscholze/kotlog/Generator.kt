package io.github.tscholze.kotlog

import com.lordcodes.turtle.shellRun
import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.PostConfiguration
import io.github.tscholze.kotlog.models.SnippetConfiguration
import io.github.tscholze.kotlog.models.YouTubeComponentConfiguration
import io.github.tscholze.kotlog.templates.components.EmbeddedYouTubeVideo
import io.github.tscholze.kotlog.templates.html.Index
import io.github.tscholze.kotlog.templates.html.Post
import io.github.tscholze.kotlog.templates.images.SocialMediaPreviewImage
import io.github.tscholze.kotlog.templates.json.ConfigurationHomeFile
import io.github.tscholze.kotlog.utils.toSlug
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.*

/**
 * Kotlog represents a static side generator which is tailored to the use case
 * of the developer behind this spare time project.
 *
 * @param args CLI arguments which will be processed
 * @param presetConfiguration Optional blog configuration, if empty a config file is required.
 *
 * Possible CLI arguments:
 *  - `-c 'My awesome title'`: Creates a new blog post
 *  - `-y beYqB6QXQuY`: Creates a YouTube post
 *  - `-g` : Generates html output
 *  - `-p` : Publish aka pushes changes to remote
 *  - `-co`: To publish output files
 *  -  `cc`: To create a new configuration file
 */
class Kotlog(args: Array<String>, presetConfiguration: BlogConfiguration? = null) {
    companion object {
        // MARK: - Internal constants -

        val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC)!!
        val WORKING_DIRECTORY = Paths.get("").toAbsolutePath().toString()

        // MARK: - Private constants -

        private val ABSOLUT_WORKING_DIRECTORY_CONFIG_PATH = "$WORKING_DIRECTORY/.kotlog"
        private val ABSOLUTE_HOME_CONFIG_PATH = System.getProperty("user.home")+"/.kotlog"
        private const val RELATIVE_POSTS_PATH = "__posts"
        private const val RELATIVE_STYLES_PATH = "__styles"

        private const val JSON_OUTPUT_FILENAME = "posts.json"
        private const val INDEX_OUTPUT_FILENAME = "index.html"

        private val REQUIRED_FOLDERS = listOf(RELATIVE_STYLES_PATH)
        private val EMBEDDED_FILENAMES = listOf("style.css", "apple-touch-icon.png", "favicon.ico", "icon.svg")
    }

    // MARK: - Private properties -

    private lateinit var configuration: BlogConfiguration

    // MARK: - Init -

    init {
        // Check if a preset configuration has been set.
        if (presetConfiguration != null) {
            run(args, presetConfiguration)
        }
        // If no preset configuration is given, try to load from file
        else {
            val configFromFile = loadConfigFromFile()
            if (configFromFile == null) {
                printConfigFileMissing()
            } else {
                run(args, configFromFile)
            }
        }
    }


    // MARK: - Run -

    private fun run(args: Array<String>, configuration: BlogConfiguration) {
        // Set resulting configuration
        this.configuration = configuration

        // Greet the user
        printGreeting()

        // 1. Ensure at least one cli argument is given
        //      -> if not, print help message
        // 2. Perform sanity check.
        //      -> if not, print error message
        // 3. Process cli arguments
        if (args.isEmpty()) {
            printHelp()
        } else if (sanityCheckRequiredFoldersAndFiles()) {
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

        val createConfig by parser.option(
            ArgType.Boolean,
            shortName = "cc",
            description = "Creates an empty config file in user's home directory"
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

            // Check if -cc is set -> Create configuration file
            createConfig == true -> {
                createConfigFile()
            }
        }
    }

    private fun processCliOpenMdFileInput(filename: String) {
        print("Open Markdown file in VSCode? (y/n)\n> ")
        val boolString = readlnOrNull()

        if (boolString == "y") {
            shellRun {
                val path = "$WORKING_DIRECTORY/$RELATIVE_POSTS_PATH/$filename"
                command("code", listOf(path))
            }
        }
    }

    private fun processCliPublishInput() {
        print("Do you want to publish the changes? (y/n)\n> ")
        val boolString = readlnOrNull()

        if (boolString == "y") {
            pushToRemote()
        }
    }

    private fun processCliNewConfigInput() {
        print("Open config file in VSCode? (y/n)\n> ")
        val boolString = readlnOrNull()

        if (boolString == "y") {
            shellRun {
                command("code", listOf(ABSOLUTE_HOME_CONFIG_PATH))
            }
        }
    }

    // MARK: - Generators -

    private fun generateMarkdownPost(title: String, content: String? = null) {
        val filename = "${DATE_FORMATTER.format(Date().toInstant())}-${title.toSlug()}.md"
        val markdown = io.github.tscholze.kotlog.templates.markdown.Post(
            title, content ?: "Your content goes here"
        ).render()

        writeToPosts(filename, markdown)
        printNewPostMessage(filename)
        processCliOpenMdFileInput(filename)
    }

    private fun generateMarkdownYoutubePost(videoId: String) {
        val videoUrl = "https://www.youtube.com/watch?v=$videoId"
        val embedUrl = "https://www.youtube.com/embed/$videoId"
        val dom = Jsoup.parse(URL(videoUrl), 3000)
        val title = dom.title().replace(" - YouTube", "").trim()

        // YouTube hides DOM elements if they are requested not by a browser
        // We have to figure out how to get the content description of a video.

        val video = YouTubeComponentConfiguration(title, videoUrl, embedUrl)
        val content = EmbeddedYouTubeVideo(video).render()
        generateMarkdownPost(title, content)
    }

    private fun generateHtmlOutput() {
        // 1. Get post configurations from markdown files
        val posts = File(RELATIVE_POSTS_PATH)
            .walk()
            .filter { it.extension == "md" }
            .map { PostConfiguration.fromFile(it) }

        // 2. Transform posts into snippets
        val snippets = posts
            .map { SnippetConfiguration.from(it, configuration.baseUrl) }
            .toList()

        // 3. Transform post configs into html
        // 4. Transform post configs into images
        posts.forEach {
            writeToOutput(it.filename, Post(configuration, it).render())
            SocialMediaPreviewImage.generate(it, configuration.outputDirectoryName)
        }

        // 5. Transform snippets into index html
        writeToOutput(
            INDEX_OUTPUT_FILENAME,
            Index(configuration, snippets).render()
        )

        // 6. Transform snippets into json feed
        writeToOutput(
            JSON_OUTPUT_FILENAME,
            Json.encodeToString(snippets)
        )

        // 7. Embed styles
        EMBEDDED_FILENAMES.forEach {
            configuration.outputDirectoryName.copyFile("$RELATIVE_STYLES_PATH/$it")
        }

        // 8. Print command to open output
        printOutputFilePath()

        // 9. Ask the user if changes should be published
        processCliPublishInput()
    }

    // MARK: - Pretty Prints -

    private fun printGreeting() {
        println("")
        println("Welcome to Kotlog <3")
    }

    private fun printHelp() {
        println("ERROR: No arguments given.")
        println("Please specify an argument what you want to do.")
        println("    -c : 'My awesome title' : To create a new Markdown post")
        println("    -y : 'xcg24fa' : To create a new YouTube video Markdown post")
        println("    -g : To generate output files")
        println("    -p : To publish output files")
        println("    -co: To publish output files")
        println("    -cc: To create a new configuration file")
    }

    private fun printOutputFilePath() {
        println("")
        println("Html has been generated to folder:")
        println("Location: $WORKING_DIRECTORY/${configuration.outputDirectoryName}")
        println("")
    }

    private fun printNewPostMessage(filenameWithExtension: String) {
        println("")
        println("New post '$filenameWithExtension' has been created!")
        println("Location: $WORKING_DIRECTORY/$RELATIVE_POSTS_PATH/$filenameWithExtension")
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

    private fun printNewConfigFileCreateMessage() {
        println("")
        println("A new configuration file has been created!")
        println("Path to file: $ABSOLUTE_HOME_CONFIG_PATH")
        println("")
    }

    private fun printConfigFileMissing() {
        println("")
        println("Error!")
        println("Cannot find any configuration file at: '$ABSOLUTE_HOME_CONFIG_PATH'")
        println("Create a skeleton for .kotlog file? (y/n)")
        if (readln() == "y") {
            createConfigFile()
        }
    }

    // MARK: - Cleaning helper -

    private fun cleanOutput() {
        File(configuration.outputDirectoryName)
            .walk()
            .filter { it.nameWithoutExtension != "assets" }
            .forEach { it.delete() }
    }

    // MARK: - Configuration helper -

    private fun createConfigFile() {
        writeToPath(
            ABSOLUTE_HOME_CONFIG_PATH,
            ConfigurationHomeFile().render()
        )

        printNewConfigFileCreateMessage()
        processCliNewConfigInput()
    }

    /**
     * Tries to load configuration json file from disk.
     *
     * It first tries to load from working directory
     * and secondly from user's directory.
     *
     * @return Found and decoded configuration, null if no files exists or wrong formatted.
     */
    private fun loadConfigFromFile(): BlogConfiguration? {
        val jsonString = readFromFile(ABSOLUT_WORKING_DIRECTORY_CONFIG_PATH) ?: readFromFile(ABSOLUTE_HOME_CONFIG_PATH)

        return if (jsonString == null) {
            null
        } else {
            try {
                Json.decodeFromString(jsonString)
            } catch (e: java.lang.Exception) {
                null
            }
        }
    }

    // MARK: - File access -

    private fun writeToPosts(filenameWithExtension: String, input: String) {
        writeToPath("$RELATIVE_POSTS_PATH/$filenameWithExtension", input)
    }

    private fun writeToOutput(filenameWithExtension: String, input: String) {
        writeToPath("${configuration.outputDirectoryName}/$filenameWithExtension", input)
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

    private fun readFromFile(filePath: String): String? {
        val path = Path(filePath)
        return if (!path.exists()) {
            null
        } else {
            try {
                path.readText()
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun String.copyFile(fromFilePath: String) {
        shellRun {
            command("cp", listOf("-f", fromFilePath, this@copyFile))
        }
    }

    // MARK: - Git -

    private fun pushToRemote() {
        val message = "Content update ${DATE_FORMATTER.format(Date().toInstant())}"
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
        REQUIRED_FOLDERS
            .forEach {
                val file = File(it)
                if (!file.exists() || !file.isDirectory || file.listFiles()!!.isEmpty()) {
                    isValid = false
                }
            }

        // Add other checks if needed ...

        return isValid
    }
}