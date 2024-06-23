package lol.dap.asgard.network.packets.outgoing.status

import lol.dap.asgard.gson.AsgardGson
import lol.dap.asgard.motd.Motd
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet

@Packet(id = 0x00)
data class S00ResponsePacket(
    val jsonResponse: String
) : OutgoingPacket {

    constructor(motd: Motd) : this(AsgardGson.toJson(motd))

}