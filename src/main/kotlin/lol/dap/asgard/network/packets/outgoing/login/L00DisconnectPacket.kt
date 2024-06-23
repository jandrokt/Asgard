package lol.dap.asgard.network.packets.outgoing.login

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet

@Packet(id = 0x00)
data class L00DisconnectPacket(
    val jsonReason: String
) : OutgoingPacket