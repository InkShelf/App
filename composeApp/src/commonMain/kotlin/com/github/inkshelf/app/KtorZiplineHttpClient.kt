package com.github.inkshelf.app

import app.cash.zipline.loader.ZiplineHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.isSuccess
import io.ktor.utils.io.read
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okio.ByteString
import okio.ByteString.Companion.toByteString
import org.lighthousegames.logging.logging

class KtorZiplineHttpClient(
    private val httpClient: HttpClient
) : ZiplineHttpClient() {

    override suspend fun download(
        url: String,
        requestHeaders: List<Pair<String, String>>
    ): ByteString {
        val response: HttpResponse = httpClient.get(url) {
            headers {
                requestHeaders.forEach { (name, value) ->
                    append(name, value)
                }
            }
        }

        if (!response.status.isSuccess()) {
            throw Exception("failed to fetch $url: ${response.status.value}")
        }

        val body = response.body<ByteArray>().copyOf()
        val byteString = body.toByteString()

        // Calculate SHA256 to verify correctness
        val sha256 = byteString.sha256().hex()
        logging().i {"Downloaded $url with SHA256: $sha256"}

        return byteString
    }

    override suspend fun openDevelopmentServerWebSocket(
        url: String,
        requestHeaders: List<Pair<String, String>>
    ): Flow<String> {
        return callbackFlow {
            val session = httpClient.webSocketSession(url) {
                requestHeaders.forEach { (name, value) ->
                    headers.append(name, value)
                }
            }

            try {
                for (frame in session.incoming) {
                    if (frame is Frame.Text) {
                        trySendBlocking(frame.readText())
                    }
                }
            } catch (e: Exception) {
                close(e)
            } finally {
                session.close()
            }
        }
    }
}