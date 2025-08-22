package net.notcoded.modfixer.mixin.screenshot_viewer;

import io.github.lgatodu47.screenshot_viewer.ScreenshotViewer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenshotViewer.class)
public class ScreenshotViewerMixin {
    @Redirect(method = "lambda$registerEvents$8", at = @At(value = "NEW", target = "(IIIIIIILnet/minecraft/resources/ResourceLocation;IILnet/minecraft/client/gui/components/Button$OnPress;Lnet/minecraft/client/gui/components/Button$OnTooltip;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/components/ImageButton;"))
    private ImageButton fixImageButton(int i, int j, int k, int l, int m, int n, int o, ResourceLocation resourceLocation, int p, int q, Button.OnPress onPress, Button.OnTooltip onTooltip, Component component) {
        return new ImageButton(i, j, k, l, m, n, o, resourceLocation, p, q, onPress, component);
    }
}