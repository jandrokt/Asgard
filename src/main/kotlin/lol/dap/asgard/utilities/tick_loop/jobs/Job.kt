package lol.dap.asgard.utilities.tick_loop

open class Job internal constructor(
    val id: Int,
    val delay: Int,
    private val task: suspend () -> Unit
) {

    var isCancelled = false

    suspend fun run() {
        if (isCancelled) return

        task()
    }

}