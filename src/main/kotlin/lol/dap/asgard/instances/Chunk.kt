package lol.dap.asgard.instances

class Chunk(
    val position: Position,
    val sections: MutableList<Section>
) {

    data class Position(
        val x: Int,
        val z: Int
    )

    data class Section(
        val y: Int,
        val blocks: List<Block>
    )

}