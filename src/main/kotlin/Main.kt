import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText

// MARK: - Life cycle -

fun main(args: Array<String>) {
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    Kotlog(args)
}

// MARK: - Kotlog -

class Kotlog(args: Array<String>) {
    companion object {
        const val RELATIVE_POSTS_PATH = "./__posts"
        const val RELATIVE_STYLES_PATH = "./__styles"
        const val RELATIVE_OUTPUT_PATH = "./__output"
        const val RELATIVE_TEMPLATES_PATH = "./__templates"

        const val DEFAULT_STYLE_NAME = "latex"
        const val DEFAULT_STYLE_OUTPUT_FILENAME = "style.css"

        const val DEFAULT_POST_TEMPLATE_NAME = "post.html"
        const val DEFAULT_SNIPPET_TEMPLATE_NAME = "snippet.html"
        const val DEFAULT_INDEX_TEMPLATE_NAME = "index.html"

        const val DEFAULT_MARDOWN_POST_TEMPLATE_NAME = "post.md"

        const val DEFAULT_INDEX_OUTPUT_FILENAME = "index.html"
        const val DEFAULT_JSON_OUTPUT_FILENAME = "posts.json"

        const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"

        const val MISSING_TEMPLATE_WARNING = "Template not found."
        const val MISSING_FRONTMATTER_TITLE_WARNING = "MISSING_TEMPLATE_WARNING"
    }

    // MARK: - Init -

    init {
        val parser = ArgParser("Kotlog - Blog generator")
        val title by parser.option(ArgType.String, shortName = "c", description = "Create new post with given title")
        val generate by parser.option(ArgType.Boolean, shortName = "g", description = "Generate blog")
        parser.parse(args)

        if (title != null && title?.isNotBlank() == true) {
            createMarkdownPost(title!!)
        } else if (generate == true) {
            generate(BlogConfiguration("Tobias Scholze | The Stuttering Nerd"))
        }
    }

    // MARK: - Private functions -

    private fun createMarkdownPost(title: String) {
        val template = readFromTemplates(DEFAULT_MARDOWN_POST_TEMPLATE_NAME)
        val dateString = SimpleDateFormat(DEFAULT_DATE_PATTERN).format(Date())
        val filename = "$dateString-${title.toSlug()}.md"
        val markdown = template
            .replace("{{title}}", title)
            .replace("{{date}}", dateString)

        writeToPosts(filename, markdown)
        printNewPostMessage(filename)
        openMarkdownPostIfNeeded(filename)
    }

    private fun openMarkdownPostIfNeeded(filename: String) {
        print("Open Markdown file? (y/n)\n> ")
        val boolString = readLine()

        if(boolString == "y") {
            Runtime.getRuntime().exec("code $RELATIVE_POSTS_PATH/$filename")
        }
    }
    private fun generate(configuration: BlogConfiguration): Boolean {

        // 1. Clean existing output if needed.
        if (configuration.alwaysClean) {
            cleanOutput()
        }

        // 2. Generate posts
        generatePosts()

        // 3. Generate landing page
        generateIndex(configuration)

        // 4. Copy style
        copyStyle(configuration.styleName)

        // 5. Generate Json
        generateJson()

        // 6. Print command to open output
        printOutputFilePath()

        // 7. Return true if generation was successful.
        return true
    }

    private fun printOutputFilePath() {
        print(
            """
                Output:\nopen ${
                Paths.get("")
                    .toAbsolutePath()
            }/$RELATIVE_OUTPUT_PATH/$DEFAULT_INDEX_OUTPUT_FILENAME
            """
        )
    }

    private fun printNewPostMessage(filenameWithExtension: String) {
        println("")
        println("New post '$filenameWithExtension' has been created!")
        println("Location: ${Paths.get("").toAbsolutePath()}/$RELATIVE_POSTS_PATH/$filenameWithExtension")
        println("Run `kotlog -g` to generate the html.")
        println("")
    }

    private fun cleanOutput() {
        File(RELATIVE_OUTPUT_PATH)
            .walk()
            .forEach { it.delete() }
    }

    private fun generatePosts() {
        File(RELATIVE_POSTS_PATH)
            .walk()
            .filter { it.extension == "md" }
            .forEach { toPostOutputFile(it) }
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

    private fun copyStyle(styleName: String) {
        val file = File("$RELATIVE_STYLES_PATH/$styleName.css")
        if (!file.exists()) return
        val style = file.readText()

        writeToOutput(DEFAULT_STYLE_OUTPUT_FILENAME, style)
    }

    private fun generatePostOutputFileName(file: File): String {
        return "${file.nameWithoutExtension}.html"
    }

    private fun inflateSnippetTemplate(snippetConfiguration: SnippetConfiguration): String {
        return readFromTemplates(DEFAULT_SNIPPET_TEMPLATE_NAME)
            .replace("{{title}}", snippetConfiguration.title)
            .replace("{{relative_url}}", snippetConfiguration.relativeUrl)
    }

    private fun inflatePostTemplate(postConfiguration: PostConfiguration): String {
        return readFromTemplates(DEFAULT_POST_TEMPLATE_NAME)
            .replace("{{title}}", postConfiguration.title)
            .replace("{{content}}", postConfiguration.innerHtml)
    }

    private fun toPostOutputFile(file: File) {
        val content = inflatePostTemplate(getPostConfiguration(file))
        writeToOutput(generatePostOutputFileName(file), content)
    }

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

    private fun getPostConfiguration(file: File): PostConfiguration {
        // Parse file's content
        val node = createParser().parse(file.readText())

        // Check for front matter
        val frontMatterVisitor = YamlFrontMatterVisitor()
        node.accept(frontMatterVisitor)
        val title = frontMatterVisitor.data["title"]?.first() ?: MISSING_FRONTMATTER_TITLE_WARNING

        // Generate inner html string
        val innerHtml = HtmlRenderer.builder().build().render(node)

        // Return created configuration
        return PostConfiguration(title, innerHtml)
    }

    private fun createParser(): Parser {
        // Setup extensions
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

// MARK: - Models -

data class BlogConfiguration(
    val title: String,
    val styleName: String = Kotlog.DEFAULT_STYLE_NAME,
    val alwaysClean: Boolean = false,
)

data class PostConfiguration(
    val title: String,
    val innerHtml: String
)

@Serializable
data class SnippetConfiguration(
    val title: String,
    val relativeUrl: String
)

// MARK: - Extensions -

fun String.toSlug() = lowercase()
    .replace("\n", " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")