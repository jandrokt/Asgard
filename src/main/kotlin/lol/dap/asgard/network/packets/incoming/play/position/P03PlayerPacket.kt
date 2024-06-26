package lol.dap.asgard.network.packets.incoming.play.position

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState

@Packet(ClientState.PLAY, 0x03)
data class P03PlayerPacket(
    val onGround: Boolean
) : IncomingPacket