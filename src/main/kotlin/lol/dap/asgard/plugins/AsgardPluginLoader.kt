package lol.dap.asgard.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

class AsgardPluginLoader : PluginLoader {

    companion object  {

        private val logger = KotlinLogging.logger {}

    }

    private val _plugins = mutableListOf<Plugin>()
    override val plugins: List<Plugin>
        get() = _plugins.toList()

    override fun loadPlugins() {
        logger.info { "Loading plugins..." }
        val start = System.currentTimeMillis()

        val path = File("plugins/")
        if (!path.exists()) {
            path.mkdirs()
        }

        val pluginFiles = path
            .listFiles()
            .filter { it.extension == "jar" }

        for (pluginFile in pluginFiles) {
            // get entry-point.txt
            val jarFile = JarFile(pluginFile)
            val jarEntry = jarFile.getJarEntry("entry-point.txt")

            if (jarEntry == null) {
                logger.warn { "Plugin ${pluginFile.name} does not contain an entry-point.txt file" }
                continue
            }

            val entryPoint = jarFile.getInputStream(jarEntry).bufferedReader().readText()

            loadPlugin(pluginFile, entryPoint)
        }

        val end = System.currentTimeMillis()
        logger.info { "Loaded ${plugins.size} plugin(s) in ${end - start}ms" }
    }

    private fun loadPlugin(pluginFile: File, entryPoint: String) {
        val classLoader = URLClassLoader(arrayOf(pluginFile.toURI().toURL()), this::class.java.classLoader)
        val pluginClass = classLoader.loadClass(entryPoint)

        val plugin = pluginClass.getDeclaredConstructor().newInstance() as Plugin

        loadPlugin(plugin)
    }

    override fun loadPlugin(plugin: Plugin) {
        _plugins.add(plugin)
        plugin.onLoad()
    }

    override fun unloadPlugin(plugin: Plugin) {
        _plugins.remove(plugin)
        plugin.onUnload()
    }

    override fun enablePlugins() {
        // sort plugins by softDependencies and then hardDependencies
        _plugins.sortBy { plugin ->
            plugin.softDependencies?.firstOrNull { dependency ->
                _plugins.any { it.name == dependency }
            }
        }

        _plugins.sortBy { plugin ->
            plugin.hardDependencies?.firstOrNull { dependency ->
                _plugins.any { it.name == dependency }
            }
        }

        for (plugin in plugins) {
            plugin.onEnable()
        }
    }

    override fun enablePlugin(plugin: Plugin) {
        logger.info { "Enabling plugin ${plugin.name}, version ${plugin.version}..." }
        plugin.onEnable()
    }

    override fun disablePlugins() {
        for (plugin in plugins) {
            plugin.onDisable()
        }
    }

    override fun disablePlugin(plugin: Plugin) {
        logger.info { "Disabling plugin ${plugin.name}..." }
        plugin.onDisable()
    }

    override fun getPlugin(name: String): Plugin? {
        return plugins.find { it.name == name }
    }

}