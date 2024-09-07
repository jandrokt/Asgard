package lol.dap.asgard.entities

import lol.dap.asgard.entities.metadata.MetadataEntry
import lol.dap.asgard.instances.instances.Instance
import lol.dap.asgard.utilities.Vec3D

open class LivingEntity(
    override val id: Int,
    override val entityType: EntityType,

    val name: String,

    override var instance: Instance,
    override var position: Vec3D
) : Entity() {

    companion object {

        const val DEFAULT_HEALTH = 20f

    }

    var alwaysShowNametag: Boolean
        get() = metadata.getMetadata<Boolean>(3, MetadataEntry.MetadataType.BYTE) == true
        set(value) {
            metadata.addMetadata(MetadataEntry(3, MetadataEntry.MetadataType.BYTE, if (value) 1.toByte() else 0.toByte()))
        }

    var health: Float
        get() = metadata.getMetadata<Float>(6, MetadataEntry.MetadataType.FLOAT) ?: DEFAULT_HEALTH
        set(value) {
            metadata.addMetadata(MetadataEntry(6, MetadataEntry.MetadataType.FLOAT, value))
        }

    var potionEffectColor: Int
        get() = metadata.getMetadata<Int>(7, MetadataEntry.MetadataType.INT) ?: 0
        set(value) {
            metadata.addMetadata(MetadataEntry(7, MetadataEntry.MetadataType.INT, value))
        }

    var isPotionEffectAmbient: Boolean
        get() = metadata.getMetadata<Boolean>(8, MetadataEntry.MetadataType.BYTE) == true
        set(value) {
            metadata.addMetadata(MetadataEntry(8, MetadataEntry.MetadataType.BYTE, if (value) 1.toByte() else 0.toByte()))
        }

    var arrowsInEntity: Byte
        get() = metadata.getMetadata(9, MetadataEntry.MetadataType.BYTE) ?: 0
        set(value) {
            metadata.addMetadata(MetadataEntry(9, MetadataEntry.MetadataType.BYTE, value))
        }

    var hasAI: Boolean
        get() = metadata.getMetadata<Boolean>(15, MetadataEntry.MetadataType.BYTE) == true
        set(value) {
            metadata.addMetadata(MetadataEntry(15, MetadataEntry.MetadataType.BYTE, if (value) 1.toByte() else 0.toByte()))
        }

    init {
        metadata.addMetadata(MetadataEntry(2, MetadataEntry.MetadataType.STRING, name))
        metadata.addMetadata(MetadataEntry(3, MetadataEntry.MetadataType.BYTE, 0.toByte()))
        metadata.addMetadata(MetadataEntry(6, MetadataEntry.MetadataType.FLOAT, DEFAULT_HEALTH))
        metadata.addMetadata(MetadataEntry(7, MetadataEntry.MetadataType.INT, 0))
        metadata.addMetadata(MetadataEntry(8, MetadataEntry.MetadataType.BYTE, 0.toByte()))
        metadata.addMetadata(MetadataEntry(9, MetadataEntry.MetadataType.BYTE, 0))
        metadata.addMetadata(MetadataEntry(15, MetadataEntry.MetadataType.BYTE, 1.toByte()))
    }

}