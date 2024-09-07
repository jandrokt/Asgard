package lol.dap.asgard.entities.metadata

import lol.dap.asgard.network.types.VariableByteBuffer
import lol.dap.asgard.network.types.extensions.toVarInt

class EntityMetadata {

    private val metadata = mutableListOf<MetadataEntry>()
    private val changed = mutableListOf<MetadataEntry>()

    fun addMetadata(entry: MetadataEntry) {
        // check if any metadata with same index exists
        val existing = metadata.firstOrNull { it.index == entry.index }

        if (existing != null) {
            // remove existing metadata
            metadata.remove(existing)
        }

        metadata.add(entry)

        changed.add(entry)
    }

    fun removeMetadata(metadata: MetadataEntry) {
        this.metadata.remove(metadata)
    }

    fun removeMetadata(index: Byte) {
        metadata.removeIf { it.index == index }
    }

    fun getMetadata(index: Byte): MetadataEntry? {
        return metadata.firstOrNull { it.index == index }
    }

    fun <T : Any> getMetadata(index: Byte, type: MetadataEntry.MetadataType): T? {
        val metadata = getMetadata(index) ?: return null

        if (metadata.type != type) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        return metadata.value as T
    }

    fun getMetadata(): List<MetadataEntry> {
        return metadata.toList()
    }

    fun getChangedMetadata(): List<MetadataEntry> {
        return changed.toList()
    }

    fun toByteArray(): ByteArray {
        return toByteArray(metadata)
    }

    fun toChangedByteArray(): ByteArray {
        return toByteArray(changed)
    }

    fun clearChangedMetadata() {
        changed.clear()
    }

    companion object {

        private fun toByteArray(metadata: List<MetadataEntry>): ByteArray {
            val buffer = VariableByteBuffer()

            for (entry in metadata) {
                buffer.put(combineIndexAndType(entry.index.toByte(), entry.type.id.toByte()))

                when (entry.type) {
                    MetadataEntry.MetadataType.BYTE -> buffer.put(if (entry.value is Byte) entry.value else (entry.value as Int).toByte())
                    MetadataEntry.MetadataType.SHORT -> buffer.putShort(if (entry.value is Short) entry.value else (entry.value as Int).toShort())
                    MetadataEntry.MetadataType.INT -> buffer.putInt(entry.value as Int)
                    MetadataEntry.MetadataType.FLOAT -> buffer.putFloat(entry.value as Float)
                    MetadataEntry.MetadataType.STRING -> buffer.putString(entry.value as String)
                    MetadataEntry.MetadataType.ITEM -> Unit
                    MetadataEntry.MetadataType.BLOCK_LOCATION -> Unit
                    MetadataEntry.MetadataType.PRECISE_LOCATION -> Unit
                }
            }

            buffer.put(0x7F.toByte())

            return buffer.toByteArray()
        }

        private fun combineIndexAndType(index: Byte, type: Byte): Byte {
            val indexPart = index.toInt() and 0x1F
            val typePart = (type.toInt() shl 5) and 0xE0

            return (indexPart or typePart).toByte()
        }

    }

}