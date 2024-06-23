package lol.dap.asgard.instances

interface ChunkProvider {

    val chunks: List<Chunk>

    fun getChunkAt(pos: Chunk.Position): Chunk?

}