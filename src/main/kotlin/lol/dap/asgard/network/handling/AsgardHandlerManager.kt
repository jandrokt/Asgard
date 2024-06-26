package lol.dap.asgard.network.handling

import lol.dap.asgard.network.handling.handlers.handshake.HandshakeHandler
import lol.dap.asgard.network.handling.handlers.login.LoginFlowHandler
import lol.dap.asgard.network.handling.handlers.play.KeepAliveHandler
import lol.dap.asgard.network.handling.handlers.play.PositionHandler
import lol.dap.asgard.network.handling.handlers.status.StatusFlowHandler
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client

class AsgardHandlerManager : HandlerManager {

    private val handlers = mutableListOf<Handler>()

    init {
        registerHandler(HandshakeHandler())

        // Login
        registerHandler(LoginFlowHandler())

        // Play
        registerHandler(KeepAliveHandler())

        registerHandler(PositionHandler())

        // Status
        registerHandler(StatusFlowHandler())
    }

    override fun registerHandler(handler: Handler) {
        handlers.add(handler)
    }

    override suspend fun passToHandlers(client: Client, packetId: Int, packet: IncomingPacket) {
        val handlers = handlers.filter { handler ->
            handler.canHandle(client.state, packetId)
        }

        for (handler in handlers) {
            handler.handle(client, client.state, packetId, packet)
        }
    }

}