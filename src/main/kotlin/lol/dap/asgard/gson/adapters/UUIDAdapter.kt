package lol.dap.asgard.gson.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.util.*

class UUIDAdapter : TypeAdapter<UUID>() {

    override fun write(out: JsonWriter, value: UUID?) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.value(value.toString())
    }

    override fun read(`in`: JsonReader): UUID? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }

        return UUID.fromString(`in`.nextString())
    }

}