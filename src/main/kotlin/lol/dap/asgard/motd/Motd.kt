package lol.dap.asgard.motd

import net.kyori.adventure.text.Component
import java.util.UUID

data class Motd(
    val version: Version,
    val players: Players,
    val description: Component,
    val favicon: Favicon? = null
) {

    data class Version(
        val name: String,
        val protocol: Int
    )

    data class Players(
        val max: Int,
        val online: Int,
        val sample: List<Sample>? = null
    ) {

        data class Sample(
            val name: String,
            val uuid: UUID
        )

    }

    data class Favicon(
        val data: String
    )

}
