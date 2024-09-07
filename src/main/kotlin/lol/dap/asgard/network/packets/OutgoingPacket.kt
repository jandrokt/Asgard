package lol.dap.asgard.network.packets

import lol.dap.asgard.network.server.Client

interface OutgoingPacket {

    suspend fun send(client: Client?, flush: Boolean = false) {
        client?.writePacket(this, flush)
    }

    suspend fun send(clients: List<Client>, flush: Boolean = false) {
        clients.forEach { it.writePacket(this, flush) }
    }

    suspend fun send(vararg clients: Client, flush: Boolean = false){
        clients.forEach { it.writePacket(this, flush) }
    }

}