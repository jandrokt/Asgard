package lol.dap.asgard.event_dispatching

class AsgardEventDispatcher : EventDispatcher {

    private val handlers = mutableMapOf<String, MutableList<EventHandler>>()

    override fun dispatch(id: String, event: Event) {
        handlers[id]?.forEach { it.handler(event) }
    }

    override fun on(id: String, handler: (Event) -> Unit): EventHandler {
        val eventHandler = EventHandler(id, handler)
        handlers.getOrPut(id) { mutableListOf() }.add(eventHandler)
        return eventHandler
    }

    override fun off(handler: EventHandler) {
        handlers[handler.id]?.remove(handler)
    }

}