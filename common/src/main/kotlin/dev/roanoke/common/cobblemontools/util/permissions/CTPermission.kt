package dev.roanoke.common.cobblemontools.util.permissions

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.permission.Permission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.util.cobblemonResource
import dev.roanoke.common.cobblemontools.CobblemonTools
import net.minecraft.util.Identifier

data class CTPermission(
    private val node: String,
    override val level: PermissionLevel
) : Permission {

    override val identifier = Identifier(CobblemonTools.MODID, this.node)

    override val literal = "${CobblemonTools.MODID}.${this.node}"
}
