package lol.dap.asgard.network.packets.outgoing.login

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder

@Packet(id = 0x02)
data class LoginSuccessPacket(
    @SerialOrder(1) val uuid: String,
    @SerialOrder(2) val username: String
) : OutgoingPacket