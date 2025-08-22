package net.notcoded.modfixer;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ModFixer implements ModInitializer, MixinCanceller {
    public static final Logger LOGGER = LogManager.getLogger("ModFixer");

    @Override
    public void onInitialize() {
    }

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        switch (mixinClassName) {
            case "org.dimdev.vanillafix.profiler.mixins.client.KeyboardMixin":
            case "net.coderbot.iris.mixin.MixinMinecraft_NoAuthInDev":
            case "net.coderbot.iris.mixin.vertices.block_rendering.MixinChunkRebuildTask":
                return true;
            default:
                return false;
        }
    }
}
