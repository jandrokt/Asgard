package lol.dap.asgard.network.handling

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState

typealias PacketHandler = suspend (Client, IncomingPacket) -> Unit

abstract class Handler {

    private val packetHandlers = mutableMapOf<PacketReference, PacketHandler>()

    private val conditionals = mutableMapOf<PacketReference, (Client) -> Boolean>()

    private val rateLimits = mutableMapOf<PacketReference, Long>()
    private var globalRateLimit: Long = Long.MAX_VALUE
    private val lastPacketTimes = mutableMapOf<Client, MutableMap<PacketReference, Long>>()
    private val lastGlobalPacketTimes = mutableMapOf<Client, Long>()

    fun on(state: ClientState, id: Int, handler: PacketHandler) {
        packetHandlers[PacketReference(state, id)] = handler
    }

    suspend fun handle(client: Client, state: ClientState, packetId: Int, packet: IncomingPacket) {
        val packetReference = PacketReference(state, packetId)

        val now = System.currentTimeMillis()
        val lastPacketTime = lastPacketTimes.getOrPut(client) { mutableMapOf() }.getOrDefault(packetReference, 0L)
        val lastGlobalPacketTime = lastGlobalPacketTimes.getOrDefault(client, 0L)

        if (now - lastPacketTime < rateLimits.getOrDefault(packetReference, Long.MAX_VALUE) ||
            now - lastGlobalPacketTime < globalRateLimit) {
            return
        }

        lastPacketTimes[client]?.set(packetReference, now)
        lastGlobalPacketTimes[client] = now

        val condition = conditionals[packetReference]
        if (condition != null && !condition(client)) {
            return
        }

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

    fun registerConditional(state: ClientState, packetId: Int, condition: (Client) -> Boolean) {
        conditionals[PacketReference(state, packetId)] = condition
    }

    fun registerRatelimit(state: ClientState, packetId: Int, maxPacketsPerSecond: Int) {
        rateLimits[PacketReference(state, packetId)] = 1000L / maxPacketsPerSecond
    }

    fun registerGlobalRatelimit(maxPacketsPerSecond: Int) {
        globalRateLimit = 1000L / maxPacketsPerSecond
    }

    private data class PacketReference(val state: ClientState, val packetId: Int)

}