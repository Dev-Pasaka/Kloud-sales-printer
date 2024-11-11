import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import data.remote.event.*
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.json.JSONObject

suspend fun main() {
    val mapper = ObjectMapper().apply {
        registerModule(KotlinModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }



    try {
        // Configure the server URL and options
        val options = IO.Options()
        options.transports = arrayOf("websocket") // Use WebSocket transport
        options.forceNew = true
        options.reconnection = true

        // Connect to the Socket.IO server
        val socket: Socket = IO.socket("http://143.42.60.140:3000/printouts", options)

        // Listener for successful connection
        socket.on(Socket.EVENT_CONNECT) {
            println("Connected to Socket.IO server!")

            // Emit a message to the server if required
            socket.emit("custom-event", "Hello, Server!")
        }

        // Listener for custom server event
        socket.on("data") { args ->
            try {
                args.forEach {
                    println(it)
                    CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        parseEvent(it.toString())
                    }
                }
            } catch (e: Exception) {
                println("Error processing data: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }

        // Listener for connection errors
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val error = args.getOrNull(0)
            println("Connection error: $error")
        }

        // Listener for disconnection
        socket.on(Socket.EVENT_DISCONNECT) {
            println("Disconnected from server.")
        }

        // Open the connection
        socket.connect()
    } catch (e: Exception) {
        println("Error: ${e.localizedMessage}")
    }
}
