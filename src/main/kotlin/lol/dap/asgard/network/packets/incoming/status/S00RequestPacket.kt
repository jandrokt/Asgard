package lol.dap.asgard.network.packets.incoming.status

import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.server.ClientState

@Packet(ClientState.STATUS, 0x00)
class StatusRequestPacket : IncomingPacket