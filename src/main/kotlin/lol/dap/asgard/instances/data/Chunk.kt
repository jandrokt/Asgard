package lol.dap.asgard.instances.data

import lol.dap.asgard.extensions.toBytes
import lol.dap.asgard.network.types.VariableByteBuffer
import lol.dap.asgard.utilities.Vec3D
import kotlin.collections.filterNot
import kotlin.collections.flatMap
import kotlin.collections.fold
import kotlin.collections.isEmpty
import kotlin.collections.map
import kotlin.collections.reversed
import kotlin.collections.toByteArray
import kotlin.collections.toList
import kotlin.collections.withIndex
import kotlin.toUShort

data class Chunk(
    val position: Position,
    val biomes: ByteArray,
    val sections: Array<Section>
) {

    fun toBytes(groundUpContinuous: Boolean): Pair<ByteArray, UShort> {
        val buffer = VariableByteBuffer()

        val serializedSections = sections
            .filterNot { it.blocks.isEmpty() }
            .map { it.toBytes() }
        val blockTypesAndMetadata = serializedSections.map { it.first }
        val blockLightBytes = serializedSections.map { it.second }
        val skylightBytes = serializedSections.map { it.third }

        buffer.putBytes(blockTypesAndMetadata.flatMap { it.toList() }.toByteArray())
        buffer.putBytes(blockLightBytes.flatMap { it.toList() }.toByteArray())
        buffer.putBytes(skylightBytes.flatMap { it.toList() }.toByteArray())
        if (groundUpContinuous)
            buffer.putBytes(biomes)

        // Calculate bitmask as UShort. Should be 1 bit per section, 1 if section is not empty and 0 if it is
        val bitmask = sections
            .reversed()
            .fold(0.toUShort()) { acc, section ->
                val mask = if (section.blocks.isEmpty()) 0 else 1
                (acc.toInt() shl 1 or mask).toUShort()
            }

        return Pair(buffer.toByteArray(), bitmask)
    }

    fun copy(): Chunk {
        return Chunk(
            position.copy(),
            biomes.clone(),
            sections.map { it.copy() }.toTypedArray()
        )
    }

    data class Position(
        val x: Int,
        val z: Int
    ) {

        companion object {

            fun fromTridimensional(vec: Vec3D): Position {
                return Position(vec.x.toInt() shr 4, vec.z.toInt() shr 4)
            }

        }

    }

    data class Section(
        val y: Int,
        val blocks: Array<Block>
    ) {

        fun toBytes(): Triple<ByteArray, ByteArray, ByteArray> {
            // Combine block types and metadata into bytes and add to buffer
            val blockTypesAndMetadata = ByteArray(8192)
            for ((i, block) in blocks.withIndex()) {
                val index = i * 2
                blockTypesAndMetadata[index] = ((block.material.id.toInt() shl 4) or block.data.toInt()).toByte()
                blockTypesAndMetadata[index + 1] = (block.material.id.toInt() shr 4).toByte()
            }

            // Combine block light nibbles into bytes and add to buffer
            val blockLightBytes = blocks.map { it.blockLight }.toBytes().toByteArray()

            // Combine skylight nibbles into bytes and add to buffer
            val skylightBytes = blocks.map { it.skylight }.toBytes().toByteArray()

            return Triple(blockTypesAndMetadata, blockLightBytes, skylightBytes)
        }

        fun copy(): Section {
            return Section(y, blocks.map { it.copy() }.toTypedArray())
        }

    }

}