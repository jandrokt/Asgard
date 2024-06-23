package lol.dap.asgard.network.packets.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerialOrder(val order: Int)