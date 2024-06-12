package network.handling

import io.github.oshai.kotlinlogging.KotlinLogging
import extensions.toHexRepresentation
import extensions.toRegularString
import network.handlers.HandlerManager
import network.handling.handlers.HandshakeHandler
import network.handling.handlers.LoginFlowHandler
import network.handling.handlers.StatusFlowHandler
import network.packets.incoming.handshake.HandshakePacket
import network.packets.IncomingPacket
import network.packets.annotations.Packet
import network.packets.incoming.login.LoginStartPacket
import network.packets.incoming.status.PingPacket
import network.packets.incoming.status.StatusRequestPacket
import network.packets.serializers.BytePacketDeserializer
import network.server.Client
import network.server.ClientState
import network.types.extensions.getVarInt
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
        registerPacketType(HandshakePacket::class)

        // Login
        registerPacketType(LoginStartPacket::class)

        // Status
        registerPacketType(StatusRequestPacket::class)
        registerPacketType(PingPacket::class)
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
            } ?: false
        } ?: throw NoSuchElementException("No packet type found for Client State $clientState and ID $packetId")

        return BytePacketDeserializer.deserialize(packetClass, packet)
    }

}