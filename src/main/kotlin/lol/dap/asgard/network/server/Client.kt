package lol.dap.asgard.network.server

import io.ktor.network.sockets.*
import lol.dap.asgard.entities.Entity
import lol.dap.asgard.network.packets.OutgoingPacket
import java.nio.ByteBuffer
import java.util.UUID

interface Client {

    var address: SocketAddress

    var uuid: UUID
    var username: String
    var state: ClientState

    var entity: Entity?

    var keepAliveId: Int
    var lastKeepAliveResponse: Long

    suspend fun readPacket(): ByteBuffer

    suspend fun writePacket(packet: ByteBuffer, flush: Boolean = false)

    suspend fun <T : OutgoingPacket> writePacket(packet: T, flush: Boolean = false)

    suspend fun disconnect(reason: String = "")

    fun startKeepAlive()

    fun stopKeepAlive()

}