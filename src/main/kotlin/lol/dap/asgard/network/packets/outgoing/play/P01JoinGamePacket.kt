package lol.dap.asgard.network.packets.outgoing.play

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder

@Packet(id = 0x01)
data class P01JoinGamePacket(
    @SerialOrder(1) val entityId: Int,
    @SerialOrder(2) val gameMode: UByte,
    @SerialOrder(3) val dimension: Byte,
    @SerialOrder(4) val difficulty: UByte,
    @SerialOrder(5) val maxPlayers: UByte,
    @SerialOrder(6) val levelType: String,
    @SerialOrder(7) val reducedDebugInfo: Boolean
) : OutgoingPacket