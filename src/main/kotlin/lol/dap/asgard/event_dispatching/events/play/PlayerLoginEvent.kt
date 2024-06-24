package lol.dap.asgard.event_dispatching.events.play

import lol.dap.asgard.event_dispatching.Event
import lol.dap.asgard.instances.instances.Instance
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client
import net.kyori.adventure.text.Component
import java.util.UUID

data class PlayerLoginEvent(
    val client: Client,
    val packet: IncomingPacket,
    var uuid: UUID,
    var username: String,

    var loginInstance: Instance? = null,

    var cancelled: Boolean = false,
    var cancelReason: Component? = null
) : Event