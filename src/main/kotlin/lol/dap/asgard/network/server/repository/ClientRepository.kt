package lol.dap.asgard.network.server.repository

import lol.dap.asgard.network.server.Client

interface ClientRepository {

    val clients: List<Client>

    fun addClient(client: Client)

    fun removeClient(client: Client)

}