# Kotlog Copilot Instructions

## Project overview
- Kotlog is a Kotlin CLI for generating a personal static blog.
- It is intentionally tailored to the repository owner's workflow and is not positioned as a production-ready general-purpose blog engine.
- The main entry point is `src/main/kotlin/Main.kt`, which invokes `Kotlog(args)`.
- It uses `SocialMediaPreviewImage.kt` and the embedded base64 image value string to create social media preview images based on the title

## Core user workflows
- `-c "Title"` creates a new Markdown blog post stub.
- `-y "YouTubeId"` creates a Markdown post for a YouTube video announcement.
- `-g` generates HTML output for missing files.
- `-fg` force-generates HTML output.
- `-co` cleans the configured output directory.
- `-cc` creates a `.kotlog` configuration file in the user's home directory.
- `-p` publishes generated content by pushing to the remote repository.

## Important paths
- `__posts/` contains source blog posts written in Markdown.
- `__styles/` contains styles that are embedded or copied into generated output.
- The output directory is configurable with `outputDirectoryName` in `.kotlog`; examples in this repository use `www` and `docs`.
- `docs/` contains example generated assets and screenshots used in the README.

## Content format
- Blog posts are Markdown files with YAML front matter.
- Required front matter keys: `title`, `date`.
- Optional front matter keys: `abstract`, `tags`.
- Dates use the `yyyy-MM-dd` format.
- Markdown parsing includes YAML front matter, autolinks, and GitHub-flavored tables.

Example post format:

```markdown
---
title: 'My post title'
date: '2026-09-19'
abstract: 'Optional summary'
tags:
  - kotlin
  - blogging
---

Post body in Markdown.
```

## Configuration format
- Configuration is loaded from `.kotlog` in the current working directory or from `~/.kotlog`.
- The file format is JSON.
- Key fields are `baseUrl`, `titleText`, `footerText`, `outputDirectoryName`, `youtubeKey`, and `socialMedia`.
- Supported social media platforms are `GITHUB`, `TWITTER`, and `MASTODON`.

## Guidance for future agents
- Prefer small changes that preserve the current personal-blog workflow.
- Keep Markdown/front-matter compatibility intact when changing post parsing or generation.
- Keep configuration examples aligned with the fields in `BlogConfiguration`.
- When documenting behavior, prefer the repository's existing terms: "Markdown posts", "HTML output", and ".kotlog configuration file".
