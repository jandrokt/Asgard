package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.types.VarInt

@Packet(id = 0x00)
data class P00KeepAlivePacket(
    val id: VarInt
) : OutgoingPacket