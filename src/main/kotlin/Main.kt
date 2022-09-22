import io.github.tscholze.kotlog.Kotlog
import io.github.tscholze.kotlog.models.BlogConfiguration

fun main(args: Array<String>) {

    val configuration = BlogConfiguration(
        titleText = "Tobias Scholze | The Stuttering Nerd",
        footerText = "Made with ❤️ without JavaScript| Kotlog | Tobias Scholze"
    )

    Kotlog(args, configuration)
}

