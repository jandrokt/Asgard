package lol.dap.asgard.network.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import lol.dap.asgard.Asgard
import lol.dap.asgard.extensions.async
import lol.dap.asgard.extensions.toRegularString
import lol.dap.asgard.network.handling.HandlerManager

class AsgardServer(
    private val host: String,
    private val port: Int
) {

    private val logger = KotlinLogging.logger {}

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
                            val packet = client.readPacket()
                            Asgard.handler.passToHandlers(client, packet)
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

}