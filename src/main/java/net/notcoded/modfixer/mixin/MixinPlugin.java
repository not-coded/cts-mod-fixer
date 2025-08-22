package net.notcoded.modfixer.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.contains("modmenu") && !FabricLoader.getInstance().isModLoaded("modmenu")) return false;
        if(mixinClassName.contains("iris") && !FabricLoader.getInstance().isModLoaded("iris")) return false;
        if(mixinClassName.contains("fabric")) {
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("fabric");
            return !container.isPresent() || !container.get().getMetadata().getVersion().getFriendlyString().contains("1.16.combat");
        }
        if(mixinClassName.contains("screenshot_viewer") && !FabricLoader.getInstance().isModLoaded("screenshot_viewer")) return false;
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
