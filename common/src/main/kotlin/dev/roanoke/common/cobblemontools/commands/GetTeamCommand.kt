package dev.roanoke.common.cobblemontools.commands

import com.cobblemon.mod.common.api.permission.CobblemonPermissions
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.permission
import com.cobblemon.mod.common.util.player
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.roanoke.common.cobblemontools.formats.CTTeam
import dev.roanoke.common.cobblemontools.util.CobblemonToolsAPI
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object GetTeamCommand {

    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("getTeam")
            .then(
                CommandManager.argument("team_id", StringArgumentType.string())
                    .executes(::execute)
            ))
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val player: ServerPlayerEntity = context.source.playerOrThrow
        val teamId: String = StringArgumentType.getString(context, "team_id")
        val team: CTTeam? = CobblemonToolsAPI.getTeamById(teamId)
        team?.give(player);
        player.sendMessage(Text.literal("Gave you Cobblemon Tools Team: ${team?.name}"))
        return Command.SINGLE_SUCCESS
    }

}