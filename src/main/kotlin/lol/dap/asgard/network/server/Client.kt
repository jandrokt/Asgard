package network.server

import io.ktor.network.sockets.*
import network.packets.OutgoingPacket
import network.types.VariableByteBuffer
import java.nio.ByteBuffer

interface Client {

    var address: SocketAddress

    var username: String
    var state: ClientState

    suspend fun readPacket(): ByteBuffer

    suspend fun writePacket(packet: ByteBuffer)

    suspend fun <T : OutgoingPacket> writePacket(packet: T)

    suspend fun disconnect(reason: String = "")

}