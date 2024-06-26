package lol.dap.asgard.instances.chunk_providers

import lol.dap.asgard.instances.data.Block
import lol.dap.asgard.instances.data.Chunk
import lol.dap.asgard.utilities.Vec3D
import kotlin.collections.getOrNull

interface ChunkProvider {

    val chunks: List<Chunk>

    fun getChunkAt(pos: Chunk.Position): Chunk?

    fun getChunkAt(x: Int, z: Int): Chunk? {
        return getChunkAt(Chunk.Position(x, z))
    }

    fun getBlockAt(x: Int, y: Int, z: Int): Block? {
        // Calculate the chunk position from x and z
        val chunkX = x shr 4
        val chunkZ = z shr 4

        // Find the chunk
        val chunk = getChunkAt(chunkX, chunkZ) ?: return null

        // Calculate the section index from y
        val sectionY = y shr 4

        // Find the section
        val section = chunk.sections.getOrNull(sectionY) ?: return null

        // Calculate the block index within the section from x, y, and z
        val blockX = x and 0xF
        val blockY = y and 0xF
        val blockZ = z and 0xF
        val blockIndex = (blockY shl 8) or (blockZ shl 4) or blockX

        // Return the block
        return section.blocks.getOrNull(blockIndex)
    }

    fun getBlockAt(vec: Vec3D) = getBlockAt(vec.x.toInt(), vec.y.toInt(), vec.z.toInt())

    fun copy(): ChunkProvider {
        return object : ChunkProvider {

            override val chunks = this@ChunkProvider.chunks
                .map { it.copy() }
                .toMutableList()

            override fun getChunkAt(pos: Chunk.Position): Chunk? {
                return chunks.find { it.position == pos }
            }

        }
    }

}