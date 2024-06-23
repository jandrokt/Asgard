package lol.dap.asgard.instances

import com.github.luben.zstd.Zstd
import lol.dap.asgard.utilities.Nibble
import lol.dap.asgard.utilities.Vec3D
import java.nio.ByteBuffer
import java.util.BitSet
import kotlin.math.ceil

class SlimeChunkProvider(
    val bytes: ByteBuffer
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
        val magic = bytes.getShort()
        if (magic != 0xB10B.toShort()) {
            throw IllegalArgumentException("Invalid magic number")
        }

        val version = bytes.get()
        val worldVersion = bytes.get()

        val xPos = bytes.getShort()
        val zPos = bytes.getShort()

        val width = bytes.getShort()
        val length = bytes.getShort()

        val bitmaskSize = ceil((width.toDouble() * length.toDouble()) / 8.0).toInt()
        val bitmask = ByteArray(bitmaskSize)
        bytes.get(bitmask)

        // reverse the bits in the bitmask
        for (i in bitmask.indices) {
            bitmask[i] = bitmask[i].reverseBits()
        }

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
        val compressedChunksSize = bytes.getInt()
        val decompressedChunksSize = bytes.getInt()

        val compressedChunks = ByteArray(compressedChunksSize)
        bytes.get(compressedChunks)

        val decompressedChunksArray = ByteArray(decompressedChunksSize)
        Zstd.decompress(decompressedChunksArray, compressedChunks)

        val decompressedChunks = ByteBuffer.wrap(decompressedChunksArray)

        val chunks = mutableListOf<Chunk>()
        for (z in 0 until properties.length) {
            for (x in 0 until properties.width) {
                val bitSetIndex = z * properties.width + x

                if (properties.chunkBitSet.get(bitSetIndex)) {
                    // 16 * 16 integers
                    val heightmap = mutableListOf<Int>()
                    for (i in 0 until 256) {
                        heightmap.add(decompressedChunks.getInt())
                    }

                    // 16 * 16 bytes
                    val biomes = mutableListOf<Byte>()
                    for (i in 0 until 256) {
                        biomes.add(decompressedChunks.get())
                    }

                    val sectionsBitmask = ByteArray(2)
                    decompressedChunks.get(sectionsBitmask)

                    val sectionsBitset = BitSet.valueOf(sectionsBitmask)

                    val sections = mutableListOf<Chunk.Section>()
                    for (i in 0 until 16) {
                        if (sectionsBitset.get(i)) {
                            val blockLight = mutableListOf<Byte>()
                            if (decompressedChunks.get() != 0.toByte()) {
                                for (j in 0 until 2048) {
                                    blockLight.add(decompressedChunks.get())
                                }
                            }

                            val blockLightNibbles = mutableListOf<Nibble>()
                            for (j in 0 until 2048) {
                                val (high, low) = Nibble.fromByte(blockLight[j])
                                blockLightNibbles.add(high)
                                blockLightNibbles.add(low)
                            }

                            val blockTypes = mutableListOf<Byte>()
                            for (j in 0 until 4096) {
                                blockTypes.add(decompressedChunks.get())
                            }

                            val data = mutableListOf<Byte>()
                            for (j in 0 until 2048) {
                                data.add(decompressedChunks.get())
                            }

                            val dataNibbles = mutableListOf<Nibble>()
                            for (j in 0 until 2048) {
                                val (high, low) = Nibble.fromByte(data[j])
                                dataNibbles.add(high)
                                dataNibbles.add(low)
                            }

                            val skyLight = mutableListOf<Byte>()
                            if (decompressedChunks.get() != 0.toByte()) {
                                for (j in 0 until 2048) {
                                    skyLight.add(decompressedChunks.get())
                                }
                            }

                            val skyLightNibbles = mutableListOf<Nibble>()
                            for (j in 0 until 2048) {
                                val (high, low) = Nibble.fromByte(skyLight[j])
                                skyLightNibbles.add(high)
                                skyLightNibbles.add(low)
                            }

                            val blocks = mutableListOf<Block>()
                            for (j in 0 until 4096) {
                                val position = Vec3D(
                                    x * 16 + (j % 16).toDouble(),
                                    heightmap[j / 16].toDouble(),
                                    z * 16 + (j / 16).toDouble()
                                )

                                val material = blockTypes[j].toInt()
                                val data = dataNibbles[j]
                                val blockLight = blockLightNibbles[j]
                                val skyLight = skyLightNibbles[j]

                                blocks.add(Block(position, material, data, blockLight, skyLight))
                            }

                            sections.add(Chunk.Section(i, blocks))
                        }
                    }

                    chunks.add(Chunk(Chunk.Position(x, z), sections))
                }
            }
        }

        return chunks
    }

    fun Byte.reverseBits(): Byte {
        var value = this.toInt()
        var result = 0
        for (i in 0 until 8) {
            result = result shl 1
            result = result or (value and 1)
            value = value shr 1
        }
        return result.toByte()
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