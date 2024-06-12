package lol.dap.asgard.network.packets.incoming.login

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState

@Packet(ClientState.LOGIN, 0x00)
data class LoginStartPacket(
    val username: String
) : IncomingPacket