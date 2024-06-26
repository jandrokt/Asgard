package lol.dap.asgard.network.handling

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client

interface HandlerManager {

    fun registerHandler(handler: Handler)

    suspend fun passToHandlers(client: Client, packetId: Int, packet: IncomingPacket)

}