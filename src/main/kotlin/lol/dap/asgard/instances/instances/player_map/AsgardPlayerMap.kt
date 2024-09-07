package lol.dap.asgard.instances.instances.player_map

import io.github.oshai.kotlinlogging.KotlinLogging
import lol.dap.asgard.entities.PlayerEntity
import lol.dap.asgard.network.packets.outgoing.play.entities.P0CSpawnPlayerPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.P13DestroyEntitiesPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.P38PlayerListItemPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P14EntityPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P15EntityRelativeMovePacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P16EntityLookPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P17EntityLookAndRelativeMovePacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P18EntityTeleportPacket
import lol.dap.asgard.network.packets.outgoing.play.entities.position.P19EntityHeadLookPacket
import lol.dap.asgard.network.types.extensions.toVarInt
import lol.dap.asgard.utilities.Vec3D
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.UUID

class AsgardPlayerMap(
    private val entityViewDistance: Int
) : PlayerMap {

    private val _players = mutableListOf<PlayerEntity>()
    override val players: List<PlayerEntity>
        get() = _players.toList()

    private val visibilityMap = mutableMapOf<PlayerEntity, MutableSet<PlayerEntity>>()

    override val size: Int
        get() = _players.size

    override suspend fun addPlayer(player: PlayerEntity) {
        _players.add(player)

        visibilityMap[player] = mutableSetOf()

        // Create a P38PlayerListItemPacket for the new player
        val packet = P38PlayerListItemPacket.AddPlayer(
            player.client!!.uuid,
            player.name,
            emptyList(),
            player.gameMode.id.toVarInt(),
            0.toVarInt(),
            true,
            Component.text(player.name)
        )

        // Send the packet to all other players
        for (otherPlayer in _players.filter { it != player }) {
            P38PlayerListItemPacket(listOf(packet)).send(otherPlayer.client)
        }

        // Send the packet to the new player with all the players in the list
        val packetsForNewPlayer = _players.map {
            P38PlayerListItemPacket.AddPlayer(
                it.client!!.uuid,
                it.name,
                emptyList(),
                it.gameMode.id.toVarInt(),
                0.toVarInt(),
                true,
                Component.text(it.name)
            )
        }
        P38PlayerListItemPacket(packetsForNewPlayer).send(player.client)
    }

    override suspend fun removePlayer(player: PlayerEntity) {
        _players.remove(player)

        // remove from visibility map
        visibilityMap.remove(player)
        // remove from other players' visibility maps
        visibilityMap.forEach { (_, players) ->
            players.remove(player)
        }

        // Send P38PlayerListItemPacket for the removed player to all online players
        P38PlayerListItemPacket(
            listOf(
                P38PlayerListItemPacket.RemovePlayer(player.client!!.uuid)
            )
        ).send(players.mapNotNull { it.client })
    }

    override fun getPlayer(name: String): PlayerEntity? {
        return _players.firstOrNull { it.name == name }
    }

    override fun getPlayer(uuid: UUID): PlayerEntity? {
        return _players.firstOrNull { it.client?.uuid == uuid }
    }

    override fun getPlayer(id: Int): PlayerEntity? {
        return _players.firstOrNull { it.id == id }
    }

    fun getPlayersWithinRange(player: PlayerEntity): List<PlayerEntity> {
        return _players.filter { it.position.distanceTo(player.position) <= entityViewDistance }
    }

    override fun clear() {
        _players.clear()
    }

    override suspend fun showToPlayers(player: PlayerEntity) {
        // Show to players within range
        val closePlayers = getPlayersWithinRange(player)
            .filter { it != player }

        if (closePlayers.isEmpty()) return

        val packet = P0CSpawnPlayerPacket(player)

        for (closePlayer in closePlayers) {
            if (player !in visibilityMap.getOrPut(closePlayer) { mutableSetOf() }) {
                packet.send(closePlayer.client)
                visibilityMap[closePlayer]!!.add(player)

                P0CSpawnPlayerPacket(closePlayer).send(player.client)
                visibilityMap[player]!!.add(closePlayer)
            }
        }
    }

    override suspend fun permanentlyHideFromPlayers(player: PlayerEntity) {
        if (player !in _players) return

        val packet = P13DestroyEntitiesPacket(player)

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
            visibilityMap[closePlayer]!!.remove(player)
        }
    }

    override suspend fun handlePlayer(player: PlayerEntity) {
        if (player !in _players) return

        if (!player.moved && !player.looked) {
            handlePlayerLackOfMovement(player)
        } else if (player.looked && !player.moved) {
            handlePlayerLook(player)
        } else if (player.moved && !player.looked) {
            handlePlayerMove(player)
        } else {
            handlePlayerMoveAndLook(player)
        }
    }

    override suspend fun handlePlayerLackOfMovement(player: PlayerEntity) {
        if (player !in _players) return

        val packet = P14EntityPacket(player)

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
        }
    }

    override suspend fun handlePlayerMove(player: PlayerEntity) {
        if (player !in _players) return

        val distance = player.position.distanceTo(player.previousPosition)
        val packet = if (distance >= 4) {
            P18EntityTeleportPacket(player)
        } else {
            P15EntityRelativeMovePacket(player)
        }

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
        }
    }

    override suspend fun handlePlayerLook(player: PlayerEntity) {
        if (player !in _players) return

        val packet = P16EntityLookPacket(player)
        val headLookPacket = P19EntityHeadLookPacket(player)

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
            headLookPacket.send(closePlayer.client)
        }
    }

    override suspend fun handlePlayerMoveAndLook(player: PlayerEntity) {
        if (player !in _players) return

        val distance = player.position.distanceTo(player.previousPosition)
        val packet = if (distance >= 4) {
            P18EntityTeleportPacket(player)
        } else {
            P17EntityLookAndRelativeMovePacket(player)
        }
        val headLookPacket = P19EntityHeadLookPacket(player)

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
            headLookPacket.send(closePlayer.client)
        }
    }

    override suspend fun syncTeleportPlayer(player: PlayerEntity) {
        if (player !in _players) return

        val packet = P18EntityTeleportPacket(player)

        for (closePlayer in visibilityMap[player] ?: emptySet()) {
            packet.send(closePlayer.client)
        }
    }

    override fun iterator(): Iterator<PlayerEntity> {
        return _players.iterator()
    }

    override fun contains(element: PlayerEntity): Boolean {
        return _players.contains(element)
    }

    override fun containsAll(elements: Collection<PlayerEntity>): Boolean {
        return _players.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return _players.isEmpty()
    }

}