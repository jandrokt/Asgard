package network.handling

import network.packets.IncomingPacket
import network.server.Client
import network.server.ClientState

typealias PacketHandler = suspend (Client, IncomingPacket) -> Unit

abstract class Handler {

    private val packetHandlers = mutableMapOf<PacketReference, PacketHandler>()

    fun on(state: ClientState, id: Int, handler: PacketHandler) {
        packetHandlers[PacketReference(state, id)] = handler
    }

    suspend fun handle(client: Client, state: ClientState, packetId: Int, packet: IncomingPacket) {
        val handlers = getHandlers(state, packetId)

        for (handler in handlers) {
            handler(client, packet)
        }
    }

    fun canHandle(state: ClientState, packetId: Int): Boolean {
        return packetHandlers.any { (packetReference, _) ->
            packetReference.state == state && packetReference.packetId == packetId
        }
    }

    private fun getHandlers(state: ClientState, packetId: Int): List<PacketHandler> {
        return packetHandlers.filter { (packetReference, _) ->
            packetReference.state == state && packetReference.packetId == packetId
        }.map { (_, handler) ->
            handler
        }
    }

    private class PacketReference(val state: ClientState, val packetId: Int)

}