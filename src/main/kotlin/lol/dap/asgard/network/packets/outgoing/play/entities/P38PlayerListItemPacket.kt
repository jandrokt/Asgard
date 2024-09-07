package lol.dap.asgard.network.packets.outgoing.play.entities

import lol.dap.asgard.network.packets.OutgoingPacket
import lol.dap.asgard.network.packets.annotations.Ignored
import lol.dap.asgard.network.packets.annotations.Optional
import lol.dap.asgard.network.packets.annotations.Packet
import lol.dap.asgard.network.packets.annotations.SerialOrder
import lol.dap.asgard.network.types.VarInt
import lol.dap.asgard.network.types.extensions.toVarInt
import net.kyori.adventure.text.Component
import java.util.UUID

@Packet(id = 0x38)
data class P38PlayerListItemPacket(
    @SerialOrder(1) val action: VarInt,
    @SerialOrder(2) val items: List<PlayerListItem>
) : OutgoingPacket {

    constructor(items: List<PlayerListItem>) : this(items.first().action.id.toVarInt(), items)

    enum class Action(val id: Int) {
        ADD_PLAYER(0),
        UPDATE_GAME_MODE(1),
        UPDATE_LATENCY(2),
        UPDATE_DISPLAY_NAME(3),
        REMOVE_PLAYER(4);
    }

    interface PlayerListItem {

        val uuid: UUID
        val action: Action

    }

    data class AddPlayer(
        @SerialOrder(1) override val uuid: UUID,
        @SerialOrder(2) val name: String,
        @SerialOrder(3) val properties: List<Property>,
        @SerialOrder(4) val gameMode: VarInt,
        @SerialOrder(5) val ping: VarInt,
        @SerialOrder(6) val hasDisplayName: Boolean,
        @SerialOrder(7) @Optional val displayName: Component?
    ) : PlayerListItem {

        @Ignored override val action = Action.ADD_PLAYER

        constructor(
            uuid: UUID,
            name: String,
            properties: List<Property>?,
            gameMode: VarInt,
            ping: VarInt,
            displayName: Component?
        ) : this(uuid, name, properties ?: emptyList(), gameMode, ping, displayName != null, displayName)

    }

    data class UpdateGameMode(
        @SerialOrder(1) override val uuid: UUID,
        @SerialOrder(2) val gameMode: VarInt
    ) : PlayerListItem {

        @Ignored override val action = Action.UPDATE_GAME_MODE

    }

    data class UpdateLatency(
        @SerialOrder(1) override val uuid: UUID,
        @SerialOrder(2) val ping: VarInt
    ) : PlayerListItem {

        @Ignored override val action = Action.UPDATE_LATENCY

    }

    data class UpdateDisplayName(
        @SerialOrder(1) override val uuid: UUID,
        @SerialOrder(2) val hasDisplayName: Boolean,
        @SerialOrder(3) @Optional val displayName: Component?
    ) : PlayerListItem {

        @Ignored override val action = Action.UPDATE_DISPLAY_NAME

        constructor(uuid: UUID, displayName: Component?) : this(uuid, displayName != null, displayName)

    }

    data class RemovePlayer(
        @SerialOrder(1) override val uuid: UUID
    ) : PlayerListItem {

        @Ignored override val action = Action.REMOVE_PLAYER

    }

    data class Property(
        @SerialOrder(1) val name: String,
        @SerialOrder(2) val value: String,
        @SerialOrder(3) val hasSignature: Boolean,
        @SerialOrder(4) @Optional val signature: String?
    ) {

        constructor(name: String, value: String, signature: String?) : this(name, value, signature != null, signature)

    }

}