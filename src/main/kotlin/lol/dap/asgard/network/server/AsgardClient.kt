package network.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import extensions.toHexRepresentation
import extensions.toRegularString
import network.packets.OutgoingPacket
import network.packets.annotations.Packet
import network.packets.serializers.BytePacketSerializer
import network.types.extensions.readVarInt
import network.types.extensions.toVarInt
import network.types.extensions.writeVarInt
import java.nio.ByteBuffer

class AsgardClient(
    private val socket: Socket
) : Client {

    private val logger = KotlinLogging.logger {}

    override var address: SocketAddress = socket.remoteAddress

    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel(autoFlush = true)

    override var username: String = ""
    override var state: ClientState = ClientState.NONE

    override suspend fun readPacket(): ByteBuffer {
        // Read the VarInt size of the packet
        val size = readChannel.readVarInt().toInt()

        val packet = ByteBuffer.wrap(readChannel.readPacket(size).readBytes())

        return packet
    }

    override suspend fun writePacket(packet: ByteBuffer) {
        // Write the VarInt size of the packet
        writeChannel.writeVarInt(packet.remaining().toVarInt())

        // Write the packet
        writeChannel.writePacket {
            writeFully(packet)
        }
    }

    override suspend fun <T : OutgoingPacket> writePacket(packet: T) {
        // get packet id (from annotation)
        val packetId = (packet::class.annotations.first() as Packet).id

        val serializedPacket = BytePacketSerializer.serialize(packet, packet::class)
        serializedPacket.putVarInt(0, packetId.toVarInt())

        writePacket(serializedPacket.toByteBuffer())

        logger.debug { "Sent a Packet ${packetId.toHexRepresentation()} to ${address.toRegularString()} ($state)" }
    }

    override suspend fun disconnect(reason: String) {
        socket.close()
    }

}