package dev.roanoke.common.cobblemontools.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.roanoke.common.cobblemontools.formats.CTTeam
import dev.roanoke.common.cobblemontools.util.CobblemonToolsAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text

object UploadTeamCommand {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("uploadTeam")
                .executes(::execute)
                .then(
                    CommandManager.argument("team_name", StringArgumentType.string())
                        .executes(::execute)
                ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player: ServerPlayerEntity = context.source.playerOrThrow
        val server: MinecraftServer = context.source.server

        val team: CTTeam = CTTeam.fromPlayerParty(player)

        team.name = try {
            StringArgumentType.getString(context, "team_name") ?: "Test"
        } catch (e: Exception) {
            "DefaultTeamName"
        }


        CoroutineScope(Dispatchers.IO).launch {
            val url: String? = CobblemonToolsAPI.uploadTeam(team)
            server.execute {
                if (url != null) {
                    player.sendMessage(Text.literal("Uploaded your Team to Cobblemon Tools: ").append(
                        Text.literal(url).styled {
                            it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        }
                    ))
                } else {
                    player.sendMessage(Text.literal("Failed to upload Team to Cobblemon Tools..."))
                }
            }
        }

        return Command.SINGLE_SUCCESS
    }
}