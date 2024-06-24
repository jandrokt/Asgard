package lol.dap.asgard.event_dispatching.events.play.keep_alive

import lol.dap.asgard.event_dispatching.Event
import lol.dap.asgard.network.server.Client

data class PlayerKeepAliveSentEvent(
    val client: Client,
    val id: Int
) : Event