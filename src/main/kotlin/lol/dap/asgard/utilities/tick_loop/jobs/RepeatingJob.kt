package lol.dap.asgard.utilities.tick_loop

class RepeatingJob internal constructor(
    id: Int,
    delay: Int,
    val interval: Int,
    task: suspend () -> Unit
) : Job(id, delay, task)