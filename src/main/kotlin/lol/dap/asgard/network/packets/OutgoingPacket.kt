package network.packets

import network.server.Client

interface OutgoingPacket {

    suspend fun send(client: Client) {
        client.writePacket(this)
    }

}