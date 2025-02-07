package com.github.inkshelf.app

import ComicSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.zipline.EngineApi
import app.cash.zipline.Zipline
import app.cash.zipline.ZiplineManifest
import app.cash.zipline.loader.DefaultFreshnessCheckerNotFresh
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier
import app.cash.zipline.loader.ZiplineLoader
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.lighthousegames.logging.logging

suspend fun loadZiplineManifest(httpClient: HttpClient, manifestUrl: String): ZiplineManifest {
    val builder = HttpRequestBuilder()
    builder.url(manifestUrl)
    val response = httpClient.get(builder)

    if (response.status.value != 200) {
        throw Exception("Couldn't load manifest")
    }

    return ZiplineManifest.decodeJson(response.body())
}

@OptIn(EngineApi::class)
suspend fun loadComicSource(): ComicSource {
    val httpClient = HttpClient(CIO) {
        install(WebSockets) {
        }
    }
    val manifestUrl = "http://localhost:8080/manifest.zipline.json"
    val manifest = loadZiplineManifest(httpClient, manifestUrl)

    // Create a Zipline instance
    val zipline = Zipline.create(
        dispatcher = Dispatchers.Default,
    )



    val loader = ZiplineLoader(
        Dispatchers.IO, ManifestVerifier.NO_SIGNATURE_CHECKS, KtorZiplineHttpClient(
            httpClient
        )
    )

    manifest.modules.entries.forEach { entry ->
        if (!entry.key.contains("atomic", ignoreCase = true)) {
            logging().i { "Ignoring ${entry.key}" }
            return@forEach
        }

        logging().i { "${entry.key} : ${entry.value}" }

        loader.load(entry.key, DefaultFreshnessCheckerNotFresh, flowOf(manifestUrl)).collect() { result ->
            when (result) {
                is LoadResult.Success -> {
                    logging().i { "Succeeded loading $entry.key" }
                    result.zipline
                }
                is LoadResult.Failure -> error(result.exception.stackTraceToString())
            }
        }


    }

    // Retrieve the exported object
    return zipline.take("exampleSource") as ComicSource
}

@Composable
@Preview
fun App() {

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val scope = rememberCoroutineScope()

            Button(onClick = {
                scope.launch {
                    loadComicSource()

                }
            }) {
                Text("Load Comics")
            }
        }
    }
}