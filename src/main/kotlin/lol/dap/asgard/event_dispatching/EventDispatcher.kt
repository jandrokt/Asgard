package lol.dap.asgard.event_dispatching

interface EventDispatcher {

    fun dispatch(id: String, event: Event)

    fun on(id: String, handler: (Event) -> Unit): EventHandler

    fun off(handler: EventHandler)

}