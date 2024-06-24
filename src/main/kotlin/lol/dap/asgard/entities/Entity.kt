package lol.dap.asgard.entities

import lol.dap.asgard.instances.instances.Instance
import lol.dap.asgard.utilities.Vec3D

interface Entity {

    val id: Int
    val entityType: EntityType

    val name: String

    var health: Double
    var maxHealth: Double

    var instance: Instance
    var position: Vec3D
    var velocity: Vec3D

}