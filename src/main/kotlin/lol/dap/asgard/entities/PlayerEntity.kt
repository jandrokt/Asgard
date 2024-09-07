package lol.dap.asgard.entities

import lol.dap.asgard.entities.metadata.EntityMetadata
import lol.dap.asgard.entities.metadata.MetadataEntry
import lol.dap.asgard.instances.instances.Instance
import lol.dap.asgard.network.server.Client
import lol.dap.asgard.utilities.Vec3D
import net.kyori.adventure.audience.Audience

class PlayerEntity(
    val client: Client? = null,

    override val id: Int,
    name: String,

    override var instance: Instance,
    override var position: Vec3D
) : LivingEntity(
    id,
    EntityType.PLAYER,
    name,
    instance,
    position
), Audience {

    var skinFlags: Byte
        get() = metadata.getMetadata(10, MetadataEntry.MetadataType.BYTE) ?: 1
        set(value) {
            metadata.addMetadata(MetadataEntry(10, MetadataEntry.MetadataType.BYTE, value))
        }

    var hideCape: Boolean
        get() = metadata.getMetadata<Boolean>(11, MetadataEntry.MetadataType.BYTE) == true
        set(value) {
            metadata.addMetadata(MetadataEntry(11, MetadataEntry.MetadataType.BYTE, if (value) 1.toByte() else 0.toByte()))
        }

    var absorptionHearts: Float
        get() = metadata.getMetadata(17, MetadataEntry.MetadataType.FLOAT) ?: 0f
        set(value) {
            metadata.addMetadata(MetadataEntry(17, MetadataEntry.MetadataType.FLOAT, value))
        }

    var score: Int
        get() = metadata.getMetadata(18, MetadataEntry.MetadataType.INT) ?: 0
        set(value) {
            metadata.addMetadata(MetadataEntry(18, MetadataEntry.MetadataType.INT, value))
        }

    init {
        metadata.addMetadata(MetadataEntry(10, MetadataEntry.MetadataType.BYTE, 1))
        metadata.addMetadata(MetadataEntry(11, MetadataEntry.MetadataType.BYTE, 0))
        metadata.addMetadata(MetadataEntry(17, MetadataEntry.MetadataType.FLOAT, 0f))
        metadata.addMetadata(MetadataEntry(18, MetadataEntry.MetadataType.INT, 0))
    }

}