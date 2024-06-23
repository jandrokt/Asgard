package lol.dap.asgard.network.handling

import lol.dap.asgard.event_dispatching.EventDispatcher
import lol.dap.asgard.network.server.Client
import java.nio.ByteBuffer

interface HandlerManager {

    fun registerHandler(handler: Handler)

    suspend fun passToHandlers(client: Client, packet: ByteBuffer)

}