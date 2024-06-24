package lol.dap.asgard.plugins

interface Plugin {

    val name: String
    val author: String
    val version: String

    val hardDependencies: List<String>?
    val softDependencies: List<String>?

    fun onLoad() {}
    fun onEnable()

    fun onDisable()
    fun onUnload() {}

}