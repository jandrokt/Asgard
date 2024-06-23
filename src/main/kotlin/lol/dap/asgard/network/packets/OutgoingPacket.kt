package lol.dap.asgard.network.packets

import lol.dap.asgard.network.server.Client

interface OutgoingPacket {

    suspend fun send(client: Client) {
        client.writePacket(this)
    }

}