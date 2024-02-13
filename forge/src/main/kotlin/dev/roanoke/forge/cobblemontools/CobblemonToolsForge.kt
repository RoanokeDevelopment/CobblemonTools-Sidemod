package dev.roanoke.forge.cobblemontools

import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.CobblemonToolsCommands
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod

@Mod(CobblemonTools.MODID)
class CobblemonToolsForge {

    init {
        with(MinecraftForge.EVENT_BUS) {
            addListener(this@CobblemonToolsForge::registerCommands)
        }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        CobblemonToolsCommands.register(e.dispatcher, e.buildContext, e.commandSelection)
    }

}
