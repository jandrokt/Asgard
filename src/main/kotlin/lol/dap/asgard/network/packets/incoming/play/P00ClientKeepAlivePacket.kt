package lol.dap.asgard.network.packets.incoming.play

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState
import lol.dap.asgard.network.types.VarInt

@Packet(ClientState.PLAY, 0x00)
data class P00ClientKeepAlivePacket(
    val id: VarInt
) : IncomingPacket