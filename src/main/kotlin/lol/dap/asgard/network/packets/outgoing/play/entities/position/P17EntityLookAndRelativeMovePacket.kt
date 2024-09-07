package lol.dap.asgard.network.packets.outgoing.play.entities.position

import lol.dap.asgard.entities.Entity
import lol.dap.asgard.extensions.toByteDegrees
import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt

@Packet(id = 0x17)
data class P17EntityLookAndRelativeMovePacket(
    @SerialOrder(1) val entityId: VarInt,
    @SerialOrder(2) var deltaX: Byte,
    @SerialOrder(3) var deltaY: Byte,
    @SerialOrder(4) var deltaZ: Byte,
    @SerialOrder(5) val yaw: Byte,
    @SerialOrder(6) val pitch: Byte,
    @SerialOrder(7) val onGround: Boolean
) : OutgoingPacket {

    constructor(entity: Entity) : this(
        entity.id.toVarInt(),
        0,
        0,
        0,
        entity.yaw.toByteDegrees(),
        entity.pitch.toByteDegrees(),
        entity.isOnGround
    ) {
        val relativePosition = (entity.position - entity.previousPosition).toFixedPoint()
        this.deltaX = relativePosition.first.toByte()
        this.deltaY = relativePosition.second.toByte()
        this.deltaZ = relativePosition.third.toByte()
    }

}