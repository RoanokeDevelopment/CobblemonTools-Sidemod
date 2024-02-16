package dev.roanoke.common.cobblemontools.commands

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.formats.CTTeam
import dev.roanoke.common.cobblemontools.util.CobblemonToolsAPI
import dev.roanoke.common.cobblemontools.util.permissions.CTPermission
import dev.roanoke.common.cobblemontools.util.permissions.CTPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object GetTeamCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("getTeam")
            .permission(CTPermissions.GET_TEAM)
            .then(
                CommandManager.argument("team_id", StringArgumentType.string())
                    .executes(::execute)
            ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player: ServerPlayerEntity = context.source.playerOrThrow
        val server: MinecraftServer = context.source.server
        val teamId: String = StringArgumentType.getString(context, "team_id")

        CoroutineScope(Dispatchers.IO).launch {
            val team: CTTeam? = CobblemonToolsAPI.getTeamById(teamId)
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