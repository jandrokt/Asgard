package lol.dap.asgard.network.packets.registry

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.incoming.handshake.H00HandshakePacket
import lol.dap.asgard.network.packets.incoming.login.L00LoginStartPacket
import lol.dap.asgard.network.packets.incoming.play.P00ClientKeepAlivePacket
import lol.dap.asgard.network.packets.incoming.play.position.P03PlayerPacket
import lol.dap.asgard.network.packets.incoming.play.position.P04PlayerPositionPacket
import lol.dap.asgard.network.packets.incoming.play.position.P05PlayerLookPacket
import lol.dap.asgard.network.packets.incoming.play.position.P06PlayerPositionAndLookPacket
import lol.dap.asgard.network.packets.incoming.status.S00RequestPacket
import lol.dap.asgard.network.packets.incoming.status.S01PingPacket
import lol.dap.asgard.network.packets.registry.serializers.BytePacketDeserializer
import lol.dap.asgard.network.server.ClientState
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class AsgardPacketRegistry : PacketRegistry {

    private val packetRegistry = mutableListOf<KClass<*>>()

    init {
        registerPacketType(H00HandshakePacket::class)

        // Login
        registerPacketType(L00LoginStartPacket::class)

        registerPacketType(P03PlayerPacket::class)
        registerPacketType(P04PlayerPositionPacket::class)
        registerPacketType(P05PlayerLookPacket::class)
        registerPacketType(P06PlayerPositionAndLookPacket::class)

        // Status
        registerPacketType(S00RequestPacket::class)
        registerPacketType(S01PingPacket::class)

        // Play
        registerPacketType(P00ClientKeepAlivePacket::class)
    }

    override fun <T : IncomingPacket> registerPacketType(packet: KClass<T>) {
        packetRegistry.add(packet)
    }

    override fun constructPacket(clientState: ClientState, packetId: Int, packet: ByteBuffer): IncomingPacket {
        val packetClass = packetRegistry.find { packetClass ->
            val packetAnnotation = packetClass.annotations.find { it is Packet } as? Packet

            packetAnnotation?.let {
                (it.id == packetId) && (it.state == clientState)
            } == true
        } ?: throw NoSuchElementException("No packet type found for Client State $clientState and ID $packetId")

        return BytePacketDeserializer.deserialize(packetClass, packet)
    }

}