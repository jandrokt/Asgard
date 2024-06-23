package lol.dap.asgard.network.packets.annotations

import lol.dap.asgard.network.server.ClientState

annotation class Packet(val state: ClientState = ClientState.NONE, val id: Int)