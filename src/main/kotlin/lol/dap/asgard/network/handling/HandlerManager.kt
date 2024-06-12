package network.handlers

import network.handling.Handler
import network.server.Client
import java.nio.ByteBuffer

interface HandlerManager {

    fun registerHandler(handler: Handler)

    suspend fun passToHandlers(client: Client, packet: ByteBuffer)

}