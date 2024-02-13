package dev.roanoke.fabric.cobblemontools;

import net.fabricmc.api.ModInitializer;

public class FabricBootstrap implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonToolsFabric.INSTANCE.onInitialize();
    }

}
