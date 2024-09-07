package lol.dap.asgard.entities

import lol.dap.asgard.entities.metadata.EntityMetadata
import lol.dap.asgard.entities.metadata.MetadataEntry
import lol.dap.asgard.instances.instances.Instance
import lol.dap.asgard.utilities.Vec3D

abstract class Entity {

    abstract val id: Int
    abstract val entityType: EntityType
    var gameMode: GameMode = GameMode.SURVIVAL

    abstract var instance: Instance

    var previousPosition: Vec3D = Vec3D(0.0, 0.0, 0.0)
    abstract var position: Vec3D
    var velocity = Vec3D(0.0, 0.0, 0.0)

    var previousIsOnGround = false
    var isOnGround = false

    var moved = false
    var looked = false

    var previousYaw = 0.0f
    var yaw = 0.0f

    var previousPitch = 0.0f
    var pitch = 0.0f

    val metadata = EntityMetadata()
    private var bitMask: Byte = 0

    var isOnFire: Boolean
        get() = isBitSet(bitMask, ON_FIRE)
        set(value) {
            bitMask = if (value) setBitMask(bitMask, ON_FIRE) else clearBitMask(bitMask, ON_FIRE)
            metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        }

    var isCrouching: Boolean
        get() = isBitSet(bitMask, CROUCHING)
        set(value) {
            bitMask = if (value) setBitMask(bitMask, CROUCHING) else clearBitMask(bitMask, CROUCHING)
            metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        }

    var isSprinting: Boolean
        get() = isBitSet(bitMask, SPRINTING)
        set(value) {
            bitMask = if (value) setBitMask(bitMask, SPRINTING) else clearBitMask(bitMask, SPRINTING)
            metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        }

    var isUsingItem: Boolean
        get() = isBitSet(bitMask, USING_ITEM)
        set(value) {
            bitMask = if (value) setBitMask(bitMask, USING_ITEM) else clearBitMask(bitMask, USING_ITEM)
            metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        }

    var isInvisible: Boolean
        get() = isBitSet(bitMask, INVISIBLE)
        set(value) {
            bitMask = if (value) setBitMask(bitMask, INVISIBLE) else clearBitMask(bitMask, INVISIBLE)
            metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        }

    var isSilent: Boolean
        get() = metadata.getMetadata<Boolean>(4, MetadataEntry.MetadataType.BYTE) == true
        set(value) {
            metadata.addMetadata(MetadataEntry(4, MetadataEntry.MetadataType.BYTE, value))
        }

    init {
        metadata.addMetadata(MetadataEntry(0, MetadataEntry.MetadataType.BYTE, bitMask))
        metadata.addMetadata(MetadataEntry(1, MetadataEntry.MetadataType.SHORT, 0.toShort())) // ?? what even is this
        metadata.addMetadata(MetadataEntry(4, MetadataEntry.MetadataType.BYTE, 0))
    }

    fun updateMetadata() {
        this.instance.updateEntityMetadata(this)
    }

    companion object {

        private const val ON_FIRE: Byte = 0x01
        private const val CROUCHING: Byte = 0x02
        private const val SPRINTING: Byte = 0x04
        private const val USING_ITEM: Byte = 0x08 // eat, drink, block, etc.
        private const val INVISIBLE: Byte = 0x10

        private fun setBitMask(original: Byte, mask: Byte): Byte {
            return (original.toInt() or mask.toInt()).toByte()
        }

        private fun clearBitMask(original: Byte, mask: Byte): Byte {
            return (original.toInt() and mask.toInt().inv()).toByte()
        }

        private fun isBitSet(original: Byte, mask: Byte): Boolean {
            return (original.toInt() and mask.toInt()) != 0
        }
        
    }

}