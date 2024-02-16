package dev.roanoke.common.cobblemontools.util.permissions

import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.Permission
import com.cobblemon.mod.common.api.permission.PermissionLevel

object CTPermissions {

    private const val COMMAND_PREFIX = "command."
    private val permissions = arrayListOf<Permission>()

    val GET_POKE_PASTE = create("${COMMAND_PREFIX}getpokepaste", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
    val GET_TEAM = create("${COMMAND_PREFIX}getteam", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
    val UPLOAD_TEAM = create("${COMMAND_PREFIX}uploadteam", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)

    fun all(): Iterable<Permission> = permissions

    private fun create(node: String, level: PermissionLevel): Permission {
        val permission = CTPermission(node, level)
        permissions += permission
        return permission
    }

}