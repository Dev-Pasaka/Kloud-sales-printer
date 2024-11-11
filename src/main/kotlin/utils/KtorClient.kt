package utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object KtorClient {
    val client = HttpClient(CIO) {
        // Install ContentNegotiation with kotlinx.serialization for JSON
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        // Install Logging for debugging requests
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        install(WebSockets) {
            this.pingInterval = 1000
            contentConverter = KotlinxWebsocketSerializationConverter(Json)

        }

        // Default headers can be added here if needed
        defaultRequest {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }
}