package dev.roanoke.fabric.cobblemontools

import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.CobblemonToolsCommands
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback


object CobblemonToolsFabric {

    fun onInitialize() {
        CobblemonTools.LOGGER.info("CobblemonTools: Loading Fabric...")

        CommandRegistrationCallback.EVENT.register(CobblemonToolsCommands::register)
        CobblemonTools.LOGGER.info("Cobblemon Tools: Registered Commands (Fabric)")
    }
}