package lol.dap.asgard.network.packets.registry

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.ClientState
import java.nio.ByteBuffer
import kotlin.reflect.KClass

interface PacketRegistry {

    fun <T : IncomingPacket> registerPacketType(packet: KClass<T>)

    fun constructPacket(clientState: ClientState, packetId: Int, packet: ByteBuffer): IncomingPacket

}