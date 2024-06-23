package lol.dap.asgard.network.server.repository

import lol.dap.asgard.network.server.Client

class AsgardClientRepository : ClientRepository {

    private val _clients = mutableListOf<Client>()
    override val clients: List<Client>
        get() = _clients.toList()

    override fun addClient(client: Client) {
        _clients.add(client)
    }

    override fun removeClient(client: Client) {
        _clients.remove(client)
    }

}