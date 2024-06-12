package network.packets.annotations

import network.server.ClientState

annotation class Packet(val state: ClientState = ClientState.NONE, val id: Int)