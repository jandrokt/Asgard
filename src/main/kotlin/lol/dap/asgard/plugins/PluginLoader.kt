package lol.dap.asgard.plugins

interface PluginLoader {

    val plugins: List<Plugin>

    fun loadPlugins()

    fun loadPlugin(plugin: Plugin)

    fun unloadPlugin(plugin: Plugin)

    fun enablePlugins()

    fun enablePlugin(plugin: Plugin)

    fun disablePlugin(plugin: Plugin)

    fun disablePlugins()

    fun getPlugin(name: String): Plugin?

}