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
import kotlin.math.abs

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

        entity.previousIsOnGround = entity.isOnGround
        entity.isOnGround = packet.onGround

        entity.instance.playerMap.handlePlayerLackOfMovement(entity as PlayerEntity)
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

        val diff = newPos - entity.position

        entity.moved = abs(diff.x) >= 0.125 || abs(diff.y) >= 0.125 || abs(diff.z) >= 0.125

        entity.position = newPos
        entity.isOnGround = packet.onGround

        entity.instance.playerChunkMap.updatePlayerChunks(entity as PlayerEntity)
        if (entity.moved) {
            entity.instance.playerMap.handlePlayerMove(entity)

            entity.moved = false

            entity.previousPosition = entity.position
            entity.previousIsOnGround = entity.isOnGround
        }
    }

    suspend fun handlePlayerLookPacket(client: Client, packet: IncomingPacket) {
        if (packet !is P05PlayerLookPacket) return

        val entity = client.entity ?: return

        entity.looked = abs(packet.yaw - entity.yaw) >= 0.125 || abs(packet.pitch - entity.pitch) >= 0.125

        entity.yaw = packet.yaw
        entity.pitch = packet.pitch
        entity.isOnGround = packet.onGround

        if (entity.looked) {
            entity.instance.playerMap.handlePlayerLook(entity as PlayerEntity)

            entity.looked = false

            entity.previousYaw = entity.yaw
            entity.previousPitch = entity.pitch
            entity.previousIsOnGround = entity.isOnGround
        }
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

        val diff = newPos - entity.position

        entity.moved = abs(diff.x) >= 0.125 || abs(diff.y) >= 0.125 || abs(diff.z) >= 0.125
        entity.looked = abs(packet.yaw - entity.yaw) >= 0.125 || abs(packet.pitch - entity.pitch) >= 0.125

        entity.position = newPos
        entity.yaw = packet.yaw
        entity.pitch = packet.pitch
        entity.isOnGround = packet.onGround

        entity.instance.playerChunkMap.updatePlayerChunks(entity as PlayerEntity)
        if (entity.moved && entity.looked) {
            entity.instance.playerMap.handlePlayerMoveAndLook(entity)

            entity.moved = false
            entity.looked = false

            entity.previousPosition = entity.position
            entity.previousYaw = entity.yaw
            entity.previousPitch = entity.pitch
            entity.previousIsOnGround = entity.isOnGround
        } else if (entity.moved) {
            entity.instance.playerMap.handlePlayerMove(entity)

            entity.moved = false

            entity.previousPosition = entity.position
            entity.previousIsOnGround = entity.isOnGround
        } else if (entity.looked) {
            entity.instance.playerMap.handlePlayerLook(entity)

            entity.looked = false

            entity.previousYaw = entity.yaw
            entity.previousPitch = entity.pitch
            entity.previousIsOnGround = entity.isOnGround
        }
    }

}