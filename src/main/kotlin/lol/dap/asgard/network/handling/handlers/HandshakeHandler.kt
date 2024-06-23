package lol.dap.asgard.network.handling.handlers

import lol.dap.asgard.network.handling.Handler
import lol.dap.asgard.network.handling.HandlerManager
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.incoming.handshake.H00HandshakePacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState

class HandshakeHandler : Handler() {

    init {
        on(ClientState.NONE, 0x00, ::handshake)
    }

    private fun handshake(client: Client, packet: IncomingPacket) {
        if (packet !is H00HandshakePacket) return

        client.state = if (packet.nextState.toInt() == 2) {
            ClientState.LOGIN
        } else {
            ClientState.STATUS
        }
    }

}