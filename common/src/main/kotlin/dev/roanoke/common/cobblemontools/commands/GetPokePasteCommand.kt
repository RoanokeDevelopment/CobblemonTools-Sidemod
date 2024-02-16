package dev.roanoke.common.cobblemontools.commands

import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.roanoke.common.cobblemontools.formats.CTTeam
import dev.roanoke.common.cobblemontools.util.permissions.CTPermissions
import dev.roanoke.common.cobblemontools.util.CobblemonToolsAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object GetPokePasteCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("getPokePaste")
                .permission(CTPermissions.GET_POKE_PASTE)
                .then(
                    CommandManager.argument("pokepaste_url", StringArgumentType.greedyString())
                        .executes(::execute)
                ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player: ServerPlayerEntity = context.source.playerOrThrow
        val server: MinecraftServer = context.source.server
        val pokepasteUrl: String = StringArgumentType.getString(context, "pokepaste_url")

        CoroutineScope(Dispatchers.IO).launch {
            val team: CTTeam? = CobblemonToolsAPI.pokePasteToCTTeam(pokepasteUrl)
            server.execute {
                if (team != null) {
                    player.sendMessage(Text.literal("Gave you Cobblemon Tools Team: ${team?.name}"))
                    team.give(player)
                } else {
                    player.sendMessage(Text.literal("Failed to get Cobblemon Tools Team."))
                }
            }
        }

        return Command.SINGLE_SUCCESS
    }

}