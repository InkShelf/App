package com.github.inkshelf.exampleSource

import ComicSource
import app.cash.zipline.Zipline

@OptIn(ExperimentalJsExport::class)
@JsExport
fun launchZipline() {
    val zipline = Zipline.get()
    zipline.bind<ComicSource>("exampleSource", ExampleComicSource())
}