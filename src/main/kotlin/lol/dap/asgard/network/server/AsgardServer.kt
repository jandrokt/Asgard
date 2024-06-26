package lol.dap.asgard.network.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import lol.dap.asgard.Asgard
import lol.dap.asgard.extensions.async
import lol.dap.asgard.extensions.toHexRepresentation
import lol.dap.asgard.extensions.toRegularString
import lol.dap.asgard.network.handling.HandlerManager
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.types.extensions.getVarInt
import java.nio.ByteBuffer
import java.util.NoSuchElementException

class AsgardServer(
    private val host: String,
    private val port: Int
) {

    companion object {

        private val logger = KotlinLogging.logger {}

    }

    private var started = false
    private val serverSocket = aSocket(SelectorManager()).tcp().bind(host, port)

    suspend fun start() {
        started = true

        logger.info { "Asgard is listening on $host:$port" }

        while (started) {
            try {
                val clientSocket = serverSocket.accept()
                val client = AsgardClient(clientSocket)

                logger.info { "Client connected from ${client.address.toRegularString()}" }

                async {
                    try {
                        while (!clientSocket.isClosed) {
                            try {
                                val packet = client.readPacket()
                                val (packetId, constructedPacket) = constructPacket(client, packet) ?: continue

                                Asgard.handler.passToHandlers(client, packetId, constructedPacket)
                            } catch (_: ClosedReceiveChannelException) {
                                client.disconnect()
                            } catch (e: Exception) {
                                logger.error(e) { "An error occurred while reading a packet from ${client.address.toRegularString()}" }
                            }
                        }
                    } catch (_: ClosedReceiveChannelException) {
                        // Most likely a client disconnect
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        client.disconnect()

                        logger.info { "Client disconnected from ${client.address.toRegularString()}" }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun constructPacket(client: Client, packet: ByteBuffer): Pair<Int, IncomingPacket>? {
        if (packet.remaining() == 0) return null

        val packetId = packet.getVarInt().toInt()

        logger.debug { "${client.address.toRegularString()} (${client.state}) sent a Packet ${packetId.toHexRepresentation()}" }

        return try {
            packetId to Asgard.packetRegistry.constructPacket(client.state, packetId, packet)
        } catch (_: NoSuchElementException) {
            logger.warn { "No packet type found for Client State ${client.state} and ID $packetId" }
            null
        } catch (e: Exception) {
            logger.error(e) { "Error while constructing incoming packet" }
            null
        }
    }

    suspend fun stop() {
        started = false
        serverSocket.close()
    }

}