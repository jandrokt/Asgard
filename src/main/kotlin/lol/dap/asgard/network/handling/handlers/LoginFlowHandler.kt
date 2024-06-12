package network.handling.handlers

import kotlinx.coroutines.delay
import extensions.async
import network.handling.Handler
import network.packets.IncomingPacket
import network.packets.incoming.login.LoginStartPacket
import network.packets.outgoing.KeepAlivePacket
import network.packets.outgoing.login.LoginPluginRequestPacket
import network.packets.outgoing.login.LoginSuccessPacket
import network.server.AsgardClient
import network.server.Client
import network.server.ClientState
import network.types.extensions.toVarInt

class LoginFlowHandler : Handler() {

    init {
        on(ClientState.LOGIN, 0x00, ::loginStart)
    }

    private suspend fun loginStart(client: Client, packet: IncomingPacket) {
        if (packet !is LoginStartPacket || client !is AsgardClient) return

        client.username = packet.username
        client.state = ClientState.PLAY

        //SetCompressionPacket((-1).toVarInt()).send(client)

        LoginPluginRequestPacket(
            messageId = 0,
            channel = "velocity:player_info",
            data = ByteArray(1) { 1 }
        ).send(client)

        LoginSuccessPacket(
            uuid = "00000000-0000-0000-0000-000000000000",
            username = "Dap",
        ).send(client)

        /*JoinGamePacket(
            entityId = 0,
            gameMode = 0u,
            dimension = 0,
            difficulty = 0u,
            maxPlayers = 1u,
            levelType = "flat",
            reducedDebugInfo = false
        ).send(client)*/

        async {
            delay(1000)
            KeepAlivePacket(0.toVarInt()).send(client)
        }
    }

}