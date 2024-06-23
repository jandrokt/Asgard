package lol.dap.asgard.network.handling

import io.github.oshai.kotlinlogging.KotlinLogging
import lol.dap.asgard.event_dispatching.EventDispatcher
import lol.dap.asgard.extensions.toHexRepresentation
import lol.dap.asgard.extensions.toRegularString
import lol.dap.asgard.network.handling.handlers.HandshakeHandler
import lol.dap.asgard.network.handling.handlers.LoginFlowHandler
import lol.dap.asgard.network.handling.handlers.StatusFlowHandler
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.incoming.handshake.H00HandshakePacket
import lol.dap.asgard.network.packets.incoming.login.L00LoginStartPacket
import lol.dap.asgard.network.packets.incoming.status.S01PingPacket
import lol.dap.asgard.network.packets.incoming.status.S00RequestPacket
import lol.dap.asgard.network.packets.serializers.BytePacketDeserializer
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState
import lol.dap.asgard.network.types.extensions.getVarInt
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class AsgardHandlerManager : HandlerManager {

    private val logger = KotlinLogging.logger {}

    private val handlers = mutableListOf<Handler>()
    private val packetTypes = mutableListOf<KClass<*>>()

    init {
        registerAllHandlers()
        registerAllPacketTypes()
    }

    private fun registerAllHandlers() {
        registerHandler(HandshakeHandler())

        // Login
        registerHandler(LoginFlowHandler())

        // Status
        registerHandler(StatusFlowHandler())
    }

    private fun registerAllPacketTypes() {
        registerPacketType(H00HandshakePacket::class)

        // Login
        registerPacketType(L00LoginStartPacket::class)

        // Status
        registerPacketType(S00RequestPacket::class)
        registerPacketType(S01PingPacket::class)
    }

    override fun registerHandler(handler: Handler) {
        handlers.add(handler)
    }

    fun <T : IncomingPacket> registerPacketType(packet: KClass<T>) {
        packetTypes.add(packet)
    }

    override suspend fun passToHandlers(client: Client, packet: ByteBuffer) {
        val packetId = packet.getVarInt().toInt()

        logger.debug { "${client.address.toRegularString()} (${client.state}) sent a Packet ${packetId.toHexRepresentation()}" }

        val incomingPacket = try {
            constructIncomingPacket(client.state, packetId, packet)
        } catch (exc: Exception) {
            exc.printStackTrace()
            return
        }

        val handlers = handlers.filter { handler ->
            handler.canHandle(client.state, packetId)
        }

        for (handler in handlers) {
            handler.handle(client, client.state, packetId, incomingPacket)
        }
    }

    private fun constructIncomingPacket(clientState: ClientState, packetId: Int, packet: ByteBuffer): IncomingPacket {
        val packetClass = packetTypes.find { packetClass ->
            val packetAnnotation = packetClass.annotations.find { it is Packet } as? Packet

            packetAnnotation?.let {
                (it.id == packetId) && (it.state == clientState)
            } == true
        } ?: throw NoSuchElementException("No packet type found for Client State $clientState and ID $packetId")

        return BytePacketDeserializer.deserialize(packetClass, packet)
    }

}