package dev.roanoke.common.cobblemontools

import dev.roanoke.common.cobblemontools.util.Config
import org.apache.logging.log4j.LogManager
import java.io.File

object CobblemonTools {

    const val MODID = "cobblemontools"
    const val CONFIG_PATH = "config/$MODID/main.json"
    val LOGGER = LogManager.getLogger()



    var config: Config = Config.load(File(CONFIG_PATH))

}