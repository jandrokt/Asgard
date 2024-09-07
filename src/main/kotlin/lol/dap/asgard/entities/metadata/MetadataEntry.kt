package lol.dap.asgard.entities.metadata

import kotlin.collections.first

/**
 * Refer to [1.8 Entity Metadata Wiki](https://wiki.vg/index.php?title=Entity_metadata&oldid=7097) for more information.
 */
data class MetadataEntry(val index: Byte, val type: MetadataType, val value: Any) {

    enum class MetadataType(val id: Int) {

        BYTE(0),
        SHORT(1),
        INT(2),
        FLOAT(3),
        STRING(4),
        ITEM(5),
        BLOCK_LOCATION(6),
        PRECISE_LOCATION(7);

        companion object {

            fun fromId(id: Int): MetadataType {
                return MetadataType.entries.first { it.id == id }
            }

        }

    }

}
