package lol.dap.asgard.entities

import lol.dap.asgard.instances.Instance
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D

class PlayerEntity(
    val client: Client? = null,

    override val id: Int,
    override val name: String,

    override var instance: Instance,
    override var position: Vec3D
) : Entity {

    override val entityType = EntityType.PLAYER

    override var health = 20.0
    override var maxHealth = 20.0

    override var velocity = Vec3D(0.0, 0.0, 0.0)

}