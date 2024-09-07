package lol.dap.asgard.instances.chunk_providers

import com.github.luben.zstd.Zstd
import lol.dap.asgard.extensions.highNibble
import lol.dap.asgard.extensions.lowNibble
import lol.dap.asgard.instances.data.Block
import lol.dap.asgard.instances.data.Chunk
import lol.dap.asgard.instances.data.Material
import lol.dap.asgard.utilities.Vec3D
import java.io.DataInputStream
import java.lang.IllegalArgumentException
import java.util.BitSet
import kotlin.collections.find
import kotlin.collections.toList
import kotlin.io.inputStream
import kotlin.math.ceil
import kotlin.ranges.until
import kotlin.toUByte

class SlimeChunkProvider(
    val bytes: DataInputStream
) : ChunkProvider {

    val properties: Properties
    private val _chunks: MutableList<Chunk>

    override val chunks: List<Chunk>
        get() = _chunks.toList()

    init {
        properties = readProperties()
        _chunks = readChunks()
    }

    override fun getChunkAt(pos: Chunk.Position): Chunk? {
        return _chunks.find { it.position == pos }
    }

    fun readProperties(): Properties {
        val magic = bytes.readShort()
        if (magic != 0xB10B.toShort()) {
            throw IllegalArgumentException("Invalid magic number")
        }

        val version = bytes.readByte()
        val worldVersion = bytes.readByte()

        val xPos = bytes.readShort()
        val zPos = bytes.readShort()

        val width = bytes.readShort()
        val length = bytes.readShort()

        val bitmaskSize = ceil((width.toDouble() * length.toDouble()) / 8.0).toInt()
        val bitmask = ByteArray(bitmaskSize)
        bytes.read(bitmask)
        val bitset = BitSet.valueOf(bitmask)

        return Properties(
            version,
            worldVersion,
            xPos,
            zPos,
            width,
            length,
            bitset
        )
    }

    fun readChunks(): MutableList<Chunk> {
        val compressedChunksSize = bytes.readInt()
        val decompressedChunksSize = bytes.readInt()

        val compressedChunkData = ByteArray(compressedChunksSize)
        val decompressedChunkData = ByteArray(decompressedChunksSize)
        bytes.read(compressedChunkData)

        Zstd.decompress(decompressedChunkData, compressedChunkData)
        val decompressedChunks = DataInputStream(decompressedChunkData.inputStream())

        val chunks = mutableListOf<Chunk>()
        for (z in 0 until properties.length) {
            for (x in 0 until properties.width) {
                val bitSetIndex = z * properties.width + x

                if (properties.chunkBitSet.get(bitSetIndex)) {
                    // 16 * 16 integers
                    val heightmap = IntArray(256)
                    for (i in 0 until 256) {
                        heightmap[i] = decompressedChunks.readInt()
                    }

                    // 16 * 16 bytes
                    val biomes = ByteArray(256)
                    decompressedChunks.read(biomes)

                    val sectionsBitmask = ByteArray(2)
                    decompressedChunks.read(sectionsBitmask)
                    val sectionsBitset = BitSet.valueOf(sectionsBitmask)

                    val sections = Array<Chunk.Section>(16) { index ->
                        if (sectionsBitset.get(index)) {
                            // Block light
                            val blockLightBytes = ByteArray(2048)
                            val blockLightNibbles = ByteArray(4096)
                            if (decompressedChunks.readBoolean()) {
                                decompressedChunks.read(blockLightBytes)

                                for (j in 0 until 2048) {
                                    val byte = blockLightBytes[j]
                                    blockLightNibbles[j * 2] = byte.lowNibble()
                                    blockLightNibbles[j * 2 + 1] = byte.highNibble()
                                }
                            }

                            val blockTypes = ByteArray(4096)
                            decompressedChunks.read(blockTypes)

                            // Block Data
                            val dataBytes = ByteArray(2048)
                            val dataNibbles = ByteArray(4096)
                            decompressedChunks.read(dataBytes)

                            for (j in 0 until 2048) {
                                val byte = dataBytes[j]
                                dataNibbles[j * 2] = byte.lowNibble()
                                dataNibbles[j * 2 + 1] = byte.highNibble()
                            }

                            // Skylight
                            val skylightBytes = ByteArray(2048)
                            val skylightNibbles = ByteArray(4096)
                            if (decompressedChunks.readBoolean()) {
                                decompressedChunks.read(skylightBytes)

                                for (j in 0 until 2048) {
                                    val byte = skylightBytes[j]
                                    skylightNibbles[j * 2] = byte.lowNibble()
                                    skylightNibbles[j * 2 + 1] = byte.highNibble()
                                }
                            }

                            val blocks = Array<Block>(4096) { blockIndex ->
                                // y z x
                                val blockX = blockIndex and 0xF
                                val blockY = (blockIndex shr 8) + (index shl 4)
                                val blockZ = (blockIndex shr 4) and 0xF

                                val position = Vec3D(
                                    blockX.toDouble(),
                                    blockY.toDouble(),
                                    blockZ.toDouble()
                                )

                                val material = Material.fromId(blockTypes[blockIndex].toUByte())
                                val data = dataNibbles[blockIndex]

                                // TODO: Fix this (maybe)
                                val blockLight = Byte.MAX_VALUE //blockLightNibbles[blockIndex]
                                val skylight = Byte.MAX_VALUE //skylightNibbles[blockIndex]

                                Block(position, material, data, blockLight, skylight)
                            }

                            Chunk.Section(index, blocks)
                        } else {
                            Chunk.Section(index, emptyArray<Block>())
                        }
                    }

                    chunks.add(Chunk(Chunk.Position(properties.xPos + x, properties.zPos + z), biomes, sections))
                } else {
                    chunks.add(
                        Chunk(
                            Chunk.Position(properties.xPos + x, properties.zPos + z),
                            byteArrayOf(),
                            emptyArray()
                        )
                    )
                }
            }
        }

        return chunks
    }

    data class Properties(
        val version: Byte,
        val worldVersion: Byte,

        val xPos: Short,
        val zPos: Short,

        val width: Short,
        val length: Short,

        val chunkBitSet: BitSet
    )

}