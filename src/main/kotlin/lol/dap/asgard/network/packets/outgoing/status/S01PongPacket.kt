package lol.dap.asgard.network.packets.outgoing.status

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet

@Packet(id = 0x01)
data class PongPacket(
    val payload: Long
) : OutgoingPacket