package network.handling.handlers

import network.handling.Handler
import network.packets.IncomingPacket
import network.packets.incoming.handshake.HandshakePacket
import network.server.Client
import network.server.ClientState

class HandshakeHandler : Handler() {

    init {
        on(ClientState.NONE, 0x00, ::handshake)
    }

    private fun handshake(client: Client, packet: IncomingPacket) {
        if (packet !is HandshakePacket) return

        client.state = if (packet.nextState.toInt() == 2) {
            ClientState.LOGIN
        } else {
            ClientState.STATUS
        }
    }

}