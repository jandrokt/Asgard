package lol.dap.asgard.event_dispatching.events.status

import lol.dap.asgard.event_dispatching.Event
import lol.dap.asgard.motd.Motd
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client

data class StatusEvent(
    val client: Client,
    val packet: IncomingPacket,
    var motd: Motd
) : Event
