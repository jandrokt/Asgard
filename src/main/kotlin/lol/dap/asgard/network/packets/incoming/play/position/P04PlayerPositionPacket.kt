package lol.dap.asgard.network.packets.incoming.play.position

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState

@Packet(ClientState.PLAY, 0x04)
data class P04PlayerPositionPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val onGround: Boolean
) : IncomingPacket