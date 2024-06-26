package lol.dap.asgard.network.handling.handlers.play

import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.network.handling.Handler
import lol.dap.asgard.network.packets.IncomingPacket
import lol.dap.asgard.network.packets.incoming.play.position.P03PlayerPacket
import lol.dap.asgard.network.packets.incoming.play.position.P04PlayerPositionPacket
import lol.dap.asgard.network.packets.incoming.play.position.P05PlayerLookPacket
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.network.server.ClientState
import lol.dap.asgard.utilities.Vec3D

class PositionHandler : Handler() {

    init {
        on(ClientState.PLAY, 0x03, ::handlePlayerPacket)
        on(ClientState.PLAY, 0x04, ::handlePlayerPositionPacket)
        on(ClientState.PLAY, 0x05, ::handlePlayerLookPacket)
        on(ClientState.PLAY, 0x06, ::handlePlayerPositionAndLookPacket)
    }

    suspend fun handlePlayerPacket(client: Client, packet: IncomingPacket) {
        if (packet !is P03PlayerPacket) return

        val entity = client.entity ?: return

        entity.isOnGround = packet.onGround
    }

    suspend fun handlePlayerPositionPacket(client: Client, packet: IncomingPacket) {
        if (packet !is P04PlayerPositionPacket) return

        val entity = client.entity ?: return

        val newPos = Vec3D(packet.x, packet.y, packet.z)

        // if distance > 100, kick player
        if (entity.position.distanceTo(newPos) > 100) {
            client.disconnect("You moved too quickly :( (Hacking?)")
            return
        }

        entity.position = newPos
        entity.isOnGround = packet.onGround
        
        entity.instance.chunkMap.updatePlayerChunks(entity as PlayerEntity)
    }

    suspend fun handlePlayerLookPacket(client: Client, packet: IncomingPacket) {
        if (packet !is P05PlayerLookPacket) return

        val entity = client.entity ?: return

        entity.yaw = packet.yaw
        entity.pitch = packet.pitch
    }

    suspend fun handlePlayerPositionAndLookPacket(client: Client, packet: IncomingPacket) {
        if (packet !is P04PlayerPositionPacket || packet !is P05PlayerLookPacket) return

        val entity = client.entity ?: return

        val newPos = Vec3D(packet.x, packet.y, packet.z)

        // if distance > 100, kick player
        if (entity.position.distanceTo(newPos) > 100) {
            client.disconnect("You moved too quickly :( (Hacking?)")
            return
        }

        entity.position = newPos
        entity.yaw = packet.yaw
        entity.pitch = packet.pitch
        entity.isOnGround = packet.onGround

        entity.instance.chunkMap.updatePlayerChunks(entity as PlayerEntity)
    }

}