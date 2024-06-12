package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet

@Packet(id = 0x40)
data class P40DisconnectPacket(
    val jsonReason: String
) : OutgoingPacket