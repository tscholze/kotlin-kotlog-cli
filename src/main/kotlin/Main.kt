import io.github.tscholze.kotlog.Kotlog
import io.github.tscholze.kotlog.models.BlogConfiguration

fun main(args: Array<String>) {

    // 1. Create a configuration for the blog
    val configuration = BlogConfiguration(
        baseUrl = "https://tscholze.github.io/blog",
        titleText = "Tobias Scholze | The Stuttering Nerd",
        footerText = "Made with ❤️ without JavaScript| Kotlog | Tobias Scholze",
        outputDirectoryName = "www"
    )

    // 2. Call and run Kotlog with command line arguments and configuration.
    Kotlog(args, configuration)
}

