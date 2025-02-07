package com.github.inkshelf.exampleSource

import Comic
import ComicSource

class ExampleComicSource : ComicSource {

    override suspend fun fetchComics(): List<Comic> {
        // Your site-specific logic here
        return listOf(
            Comic("Example Comic", "https://example.com/comic1")
            // ...
        )
    }
}