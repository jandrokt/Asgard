package lol.dap.asgard.event_dispatching.events

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.event_dispatching.Event
import lol.dap.asgard.network.server.Client

data class PlayerEntityCreationEvent (
    val client: Client,
    var entity: PlayerEntity
) : Event