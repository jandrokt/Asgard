package lol.dap.asgard.extensions

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val Gson: Gson = GsonBuilder().disableHtmlEscaping().create()