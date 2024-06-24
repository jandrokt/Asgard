package lol.dap.asgard.event_dispatching.events.play.keep_alive

import lol.dap.asgard.event_dispatching.Event
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.server.Client

data class PlayerKeepAliveReceivedEvent(
    val client: Client,
    val packet: IncomingPacket,
    val id: Int
) : Event