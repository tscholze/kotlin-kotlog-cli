import io.github.tscholze.kotlog.Kotlog
import io.github.tscholze.kotlog.models.BlogConfiguration
import io.github.tscholze.kotlog.models.SocialMedia
import io.github.tscholze.kotlog.models.SocialMediaPlatform

fun main(args: Array<String>) {

    // 1. Create a configuration for the blog,

    // 1.1
    // fetched from `~/.kotlog file`
    //
    // 1.2.
    // Use a configuration object in code.
    /*
    val configuration = BlogConfiguration(
        baseUrl = "https://tscholze.github.io/blog",
        titleText = "Tobias Scholze | The Stuttering Nerd",
        footerText = "Made with ❤️ without JavaScript| Kotlog | Tobias Scholze",
        outputDirectoryName = "www",
        socialMedia = listOf(
            SocialMedia(SocialMediaPlatform.GITHUB, "tscholze"),
            SocialMedia(SocialMediaPlatform.TWITTER, "tobonautilus"),
            SocialMedia(SocialMediaPlatform.MASTODON, "@tobonaut@mastodon.social", "https://mastodon.social/@tobonaut")
        )
    )*/

    // 2. Call and run Kotlog with command line arguments and configuration.
    Kotlog(args)
}