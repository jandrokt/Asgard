package lol.dap.asgard.network.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import lol.dap.asgard.Asgard
import lol.dap.asgard.entities.Entity
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.play.keep_alive.PlayerKeepAliveSentEvent
import lol.dap.asgard.extensions.toHexRepresentation
import lol.dap.asgard.extensions.toRegularString
import lol.dap.asgard.gson.AsgardGson
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.outgoing.login.L00DisconnectPacket
import lol.dap.asgard.network.packets.outgoing.play.P00ServerKeepAlivePacket
import lol.dap.asgard.network.packets.outgoing.play.P40DisconnectPacket
import lol.dap.asgard.network.packets.registry.serializers.BytePacketSerializer
import lol.dap.asgard.network.types.extensions.readVarInt
import lol.dap.asgard.network.types.extensions.toVarInt
import lol.dap.asgard.network.types.extensions.writeVarInt
import net.kyori.adventure.text.Component
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class AsgardClient(
    private val socket: Socket
) : Client {

    companion object {

        private val logger = KotlinLogging.logger {}
        private val executor = Executors.newSingleThreadScheduledExecutor()

        private const val MAX_SECONDS_WITHOUT_KEEP_ALIVE = 30

    }

    override var address: SocketAddress = socket.remoteAddress

    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel(autoFlush = true)

    override var uuid: UUID = UUID.nameUUIDFromBytes("NoUUID".toByteArray())
    override var username: String = ""
    override var state: ClientState = ClientState.NONE

    override var entity: Entity? = null

    override var keepAliveId: Int = 0
    override var lastKeepAliveResponse: Long = 0
    var keepAliveScheduledFuture: ScheduledFuture<*>? = null

    override suspend fun readPacket(): ByteBuffer {
        if (readChannel.isClosedForRead || socket.isClosed) return ByteBuffer.allocate(0)

        // Read the VarInt size of the packet
        val size = readChannel.readVarInt().toInt()

        val packet = ByteBuffer.wrap(readChannel.readPacket(size).readBytes())

        return packet
    }

    override suspend fun writePacket(packet: ByteBuffer, flush: Boolean) {
        if (writeChannel.isClosedForWrite || socket.isClosed) return

        runBlocking {
            // Write the VarInt size of the packet
            writeChannel.writeVarInt(packet.remaining().toVarInt())

            // Write the packet
            writeChannel.writePacket {
                writeFully(packet)
            }

            if (flush) {
                writeChannel.flush()
            }
        }
    }

    override suspend fun <T : OutgoingPacket> writePacket(packet: T, flush: Boolean) {
        if (writeChannel.isClosedForWrite || socket.isClosed) return

        // Get the Packet ID from the annotation
        val packetId = (packet::class.annotations.first() as Packet).id

        val serializedPacket = BytePacketSerializer.serialize(packet)
        serializedPacket.putVarInt(0, packetId.toVarInt())

        writePacket(serializedPacket.toByteBuffer(), flush)

        logger.debug { "Sent a Packet ${packetId.toHexRepresentation()} to ${address.toRegularString()} ($state)" }
    }

    suspend fun disconnect(reason: Component) {
        disconnect(AsgardGson.toJson(reason))
    }

    override suspend fun disconnect(reason: String) {
        if (state == ClientState.DISCONNECTED) return

        if (entity != null) {
            entity!!.instance.removeEntity(entity!!)
        }

        stopKeepAlive()

        // Depending on the client state we might need to use different disconnect packets or just close the connection
        when (state) {
            ClientState.PLAY -> {
                P40DisconnectPacket(reason).send(this)
            }

            ClientState.LOGIN -> {
                L00DisconnectPacket(reason).send(this)
            }

            else -> Unit
        }

        state = ClientState.DISCONNECTED

        logger.info { "Disconnected ${address.toRegularString()} ($state) for reason: $reason" }

        readChannel.cancel()
        writeChannel.close()

        socket.close()
    }

    override fun startKeepAlive() {
        keepAliveScheduledFuture = executor.scheduleAtFixedRate({
            runBlocking {
                if (System.currentTimeMillis() - lastKeepAliveResponse > MAX_SECONDS_WITHOUT_KEEP_ALIVE * 1000
                    && lastKeepAliveResponse != 0L) {
                    disconnect("Timed out")
                    return@runBlocking
                }

                keepAliveId = Random.nextInt()

                val event = PlayerKeepAliveSentEvent(this@AsgardClient, keepAliveId)
                Asgard.eventDispatcher.dispatch(AsgardEvents.PLAYER_KEEP_ALIVE_SENT, event)

                writePacket(P00ServerKeepAlivePacket(event.id.toVarInt()))
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    override fun stopKeepAlive() {
        keepAliveScheduledFuture?.cancel(false)
    }

}