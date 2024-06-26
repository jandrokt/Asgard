package lol.dap.asgard.network.handling.handlers.play

import lol.dap.asgard.Asgard
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.play.keep_alive.PlayerKeepAliveReceivedEvent
import lol.dap.asgard.network.handling.Handler
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.incoming.play.P00ClientKeepAlivePacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState

class KeepAliveHandler : Handler() {

    init {
        on(ClientState.PLAY, 0x00, ::keepAlive)
    }

    private suspend fun keepAlive(client: Client, packet: IncomingPacket) {
        if (packet !is P00ClientKeepAlivePacket) return

        val event = PlayerKeepAliveReceivedEvent(client, packet, packet.id.toInt())
        Asgard.eventDispatcher.dispatch(AsgardEvents.PLAYER_KEEP_ALIVE_RECEIVED, event)

        if (client.keepAliveId != event.id.toInt()) {
            client.disconnect("Keep Alive ID mismatch!")
            return
        }

        client.lastKeepAliveResponse = System.currentTimeMillis()
    }

}