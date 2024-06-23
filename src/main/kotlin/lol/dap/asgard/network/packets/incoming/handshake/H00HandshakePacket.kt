package lol.dap.asgard.network.packets.incoming.handshake

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState
import lol.dap.asgard.network.types.VarInt

@Packet(ClientState.NONE, 0x00)
data class H00HandshakePacket(
    val protocolVersion: VarInt,
    val serverAddress: String,
    val serverPort: UShort,
    val nextState: VarInt
) : IncomingPacket