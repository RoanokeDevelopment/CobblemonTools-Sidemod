package dev.roanoke.common.cobblemontools

import com.mojang.brigadier.CommandDispatcher
import dev.roanoke.common.cobblemontools.commands.GetTeamCommand
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobblemonToolsCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandManager.RegistrationEnvironment) {
        GetTeamCommand.register(dispatcher)
    }

}