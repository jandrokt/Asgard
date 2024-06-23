package lol.dap.asgard.network.handling.handlers

import kotlinx.coroutines.delay
import lol.dap.asgard.Asgard
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.event_dispatching.AsgardEvents
import lol.dap.asgard.event_dispatching.events.PlayerEntityCreationEvent
import lol.dap.asgard.event_dispatching.events.PlayerLoginEvent
import lol.dap.asgard.extensions.async
import lol.dap.asgard.network.handling.Handler
import lol.dap.asgard.network.handling.HandlerManager
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.incoming.login.L00LoginStartPacket
import lol.dap.asgard.network.packets.outgoing.login.L02LoginSuccessPacket
import lol.dap.asgard.network.packets.outgoing.play.P00KeepAlivePacket
import lol.dap.asgard.network.server.AsgardClient
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState
import lol.dap.asgard.network.types.extensions.toVarInt
import java.util.UUID

class LoginFlowHandler : Handler() {

    init {
        on(ClientState.LOGIN, 0x00, ::loginStart)
    }

    private suspend fun loginStart(client: Client, packet: IncomingPacket) {
        if (packet !is L00LoginStartPacket || client !is AsgardClient) return

        client.uuid = UUID.nameUUIDFromBytes("OfflinePlayer:${client.username}".toByteArray())
        client.username = packet.username
        client.state = ClientState.PLAY

        val loginEvent = PlayerLoginEvent(client, packet, client.uuid, client.username, null)
        Asgard.eventDispatcher.dispatch(AsgardEvents.PLAYER_LOGIN, loginEvent)

        if (loginEvent.cancelled) {
            if (loginEvent.cancelReason != null) {
                client.disconnect(loginEvent.cancelReason!!)
            } else {
                client.disconnect("Login failed!")
            }
            return
        }

        L02LoginSuccessPacket(
            uuid = loginEvent.uuid.toString(),
            username = loginEvent.username
        ).send(client)

        if (loginEvent.loginInstance == null) {
            client.disconnect("No instance was assigned to you during login!")
            return
        }

        loginEvent.loginInstance!!.addClient(client)

        async {
            delay(1000)
            P00KeepAlivePacket(0.toVarInt()).send(client)
        }
    }

}