package lol.dap.asgard.gson

import com.google.gson.Gson
import lol.dap.asgard.gson.adapters.UUIDAdapter
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.util.*

val AsgardGson: Gson = GsonComponentSerializer.colorDownsamplingGson().serializer().newBuilder()
    .registerTypeAdapter(UUID::class.java, UUIDAdapter())
    .create()
