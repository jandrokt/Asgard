package lol.dap.asgard.network.handling.handlers

import lol.dap.asgard.Asgard
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.StatusEvent
import lol.dap.asgard.motd.Motd
import lol.dap.asgard.network.handling.Handler
import lol.dap.asgard.network.handling.HandlerManager
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.incoming.status.S01PingPacket
import lol.dap.asgard.network.packets.outgoing.status.S01PongPacket
import lol.dap.asgard.network.packets.outgoing.status.S00ResponsePacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState
import net.kyori.adventure.text.minimessage.MiniMessage

class StatusFlowHandler : Handler() {

    companion object {
        private val mm = MiniMessage.miniMessage()

       private val default = Motd(
            Motd.Version(
                "Asgard",
                47
            ),
            Motd.Players(
                max = 20,
                online = 0
            ),
            mm.deserialize("<rainbow>An Asgard Server</rainbow>")
        )
    }

    init {
        on(ClientState.STATUS, 0x00, ::status)
        on(ClientState.STATUS, 0x01, ::ping)
    }

    private suspend fun status(client: Client, packet: IncomingPacket) {
        val event = StatusEvent(client, packet, default)
        Asgard.eventDispatcher.dispatch(AsgardEvents.STATUS, event)

        // Send status
        S00ResponsePacket(event.motd).send(client)
    }

    private suspend fun ping(client: Client, packet: IncomingPacket) {
        if (packet !is S01PingPacket) return

        // Send pong
        S01PongPacket(packet.payload).send(client)
    }

}