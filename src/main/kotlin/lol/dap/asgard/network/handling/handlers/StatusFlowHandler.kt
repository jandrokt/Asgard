package network.handling.handlers

import network.handling.Handler
import network.packets.IncomingPacket
import network.packets.incoming.status.PingPacket
import network.packets.outgoing.status.PongPacket
import network.packets.outgoing.status.StatusResponsePacket
import network.server.Client
import network.server.ClientState

class StatusFlowHandler : Handler() {

    init {
        on(ClientState.STATUS, 0x00, ::status)
        on(ClientState.STATUS, 0x01, ::ping)
    }

    private suspend fun status(client: Client, packet: IncomingPacket) {
        // Send status
        StatusResponsePacket("""
            {
                "version": {
                    "name": "Asgard",
                    "protocol": 47
                },
                "players": {
                    "max": 100,
                    "online": 0
                },	
                "description": {
                    "text": "Hello world"
                }
            }
        """.trimIndent()).send(client)
    }

    private suspend fun ping(client: Client, packet: IncomingPacket) {
        if (packet !is PingPacket) return

        // Send pong
        PongPacket(packet.payload).send(client)
    }

}