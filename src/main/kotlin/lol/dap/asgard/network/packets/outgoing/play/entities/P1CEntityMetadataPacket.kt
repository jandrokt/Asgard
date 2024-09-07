package lol.dap.asgard.network.packets.outgoing.play.entities

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x1C)
data class P1CEntityMetadataPacket(
    @SerialOrder(1) val entityId: VarInt,
    @SerialOrder(2) val metadata: ByteArray
) : OutgoingPacket {

    constructor(entity: Entity) : this(entity.id.toVarInt(), entity.metadata.toChangedByteArray())

}