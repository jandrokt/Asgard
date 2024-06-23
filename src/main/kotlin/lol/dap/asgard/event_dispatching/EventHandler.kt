package lol.dap.asgard.event_dispatching

data class EventHandler(
    val id: String,
    val handler: (Event) -> Unit
)