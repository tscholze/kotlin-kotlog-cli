# kotlin-kotlog-cli
**Kot**lin B**log** is a CLI static blog generator - not intended for public or productional use. It features some handy shortcuts to streamline my content creation workflows.

## tl;dr
No, I do not think that you wanna use this Kotlin CLI. It is meant as a content creation helper for me needs. But if you find it interessering, awesome!

## Why?
I was looking for a simple static site blog generator. Jekyll and others were to over powered and too complex for my needs. Besides this, I wanted to have a more depth look into learning Koltin and going back to JetBrains IDEs.

Besides this, I wanted to streamline my content creation workflows by letting do the programm do the heavy lifting in looking for information that are required for the resulting blog post.

## Features
- Creating new blog post markdown files
- Supports Frontmatter for meta information
- Rendering markdown files into html blog articles
- Simple html styled by "drop-in" CSS themes
- Rendering a feed.json with snippets of blog articles
- Shortcut for creating blog posts by just providing e.g. a YouTube video id

## Ideas
- Switching to Kobweb / Compose for Web instead of html templates
- Embedding "auto share" of new blog posts
- Creating "social cards" that looks nice if you share the post on social media

## Warning
Do not use this tool in production or something else besides education. I just started studying Kotlin and I have no idea if the source that's generated or consumed by the CSS is GDPR or something else complient.

If you have any other warnings for me, please open an issue. I'm here to learn!

## Thanks to
The app is built on the work of giants. Without the following folks, repositories and posts, my tiny project would not exist.

- [David Herman (bitspittle)](https://github.com/bitspittle) - one of the most aspiring Kotlin educator and developer of Kobweb
- [Marcel Reiter (MarcelReiter)](https://github.com/MarcelReiter) - an awesome colleage and for never being tired of my beginners questions
- [Liam Doherty (dohliam)](https://github.com/dohliam/dropin-minimal-css#theme-collections) for curating the drop-in css collection which is used as `style` in the app