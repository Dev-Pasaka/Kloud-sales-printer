package data.repository

import common.Resource
import data.remote.event.Event
import data.remote.event.parseEvent
import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getReceiptsRes.UpdatedPrintedReceiptsRes
import data.remote.response.getZReport.GetZreportRes
import domain.repository.GetReceiptsRepository
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.websocket.*
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utils.KeyValueStorage
import utils.KtorClient
import utils.KtorClient.client
import java.nio.ByteBuffer
import java.util.*

class GetReceiptsRepositoryImpl(
) : GetReceiptsRepository {

    override suspend fun listenReceipts(): Flow<Resource<Event>> = channelFlow {
        try {
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
                trySend(Resource.Loading(message = "Connected to Socket.IO server!"))

            }

            // Listener for custom server event
            socket.on("data") { args ->
                try {
                    args.forEach { event ->
                        println(event)
                        val data = parseEvent(event.toString())
                        trySend(
                            Resource.Success(
                                data = data,
                                message = "Success"
                            )
                        )

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
                trySend(Resource.Loading(message = "Connected to Socket.IO server!"))
            }

            // Listener for disconnection
            socket.on(Socket.EVENT_DISCONNECT) {
                println("Disconnected from server.")
                trySend(Resource.Loading(message = "Connected to Socket.IO server!"))

            }

            // Open the connection
            socket.connect()
            // Keep the channel open and reconnect on disconnection
            while (isActive) {
                if (!socket.connected()) {
                    println("Reconnecting...")
                    try {
                        socket.connect()
                    } catch (e: Exception) {
                        println("Reconnection failed: ${e.localizedMessage}")
                        trySend(Resource.Error("Reconnection failed: ${e.localizedMessage}"))
                    }
                }
                delay(5000) // Adjust the delay as needed
            }

            // Clean up when the flow is closed
            awaitClose {
                socket.disconnect()
                socket.off() // Remove all listeners
                println("Socket disconnected and flow closed.")
            }
        } catch (e: Exception) {
            println("Error: ${e.localizedMessage}")
        }
    }


}

suspend fun main() {
    GetReceiptsRepositoryImpl().listenReceipts().collect {event->
        when(event){
            is Resource.Success -> {
                val data = event.data
                when(data){
                    is Event.Receipt ->{
                        val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(data.receipt)
                        ReceiptRepositoryImpl().generateImage(
                            html = html,
                            receiptId = data.receipt.id.toString()
                        )
                    }else->{

                    }
                }
            }
            else ->{

            }
        }
    }
}