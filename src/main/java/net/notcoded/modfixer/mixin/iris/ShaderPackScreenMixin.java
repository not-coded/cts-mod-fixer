package net.notcoded.modfixer.mixin.iris;

import com.llamalad7.mixinextras.sugar.Local;
import net.coderbot.iris.gui.GuiUtil;
import net.coderbot.iris.gui.element.ShaderPackOptionList;
import net.coderbot.iris.gui.element.ShaderPackSelectionList;
import net.coderbot.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderPackScreen.class)
public abstract class ShaderPackScreenMixin extends Screen {

    protected ShaderPackScreenMixin(Component component) {
        super(component);
    }

    @Shadow
    private boolean guiHidden;

    @Shadow
    protected abstract void init();

    // method_31322 >> setRenderSelection(boolean bl)
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/coderbot/iris/gui/element/ShaderPackOptionList;method_31322(Z)V"))
    private void fixShaderPackOptionList(ShaderPackOptionList instance, boolean b) {
        instance.setRenderSelection(b);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/coderbot/iris/gui/element/ShaderPackSelectionList;method_31322(Z)V"))
    private void fixShaderPackSelectionList(ShaderPackSelectionList instance, boolean b) {
        instance.setRenderSelection(b);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ImageButton;<init>(IIIIIIILnet/minecraft/resources/ResourceLocation;IILnet/minecraft/client/gui/components/Button$OnPress;Lnet/minecraft/client/gui/components/Button$OnTooltip;Lnet/minecraft/network/chat/Component;)V"), cancellable = true)
    private void fixImageButton(CallbackInfo ci, @Local TranslatableComponent showOrHide, @Local(ordinal = 3) int x) {
        ci.cancel();

        this.addButton(new ImageButton(x, this.height - 39, 20, 20, this.guiHidden ? 20 : 0, 146, 20, GuiUtil.IRIS_WIDGETS_TEX, 256, 256, (var1x) -> {
            this.guiHidden = !this.guiHidden;
            this.init();
        }, showOrHide));
    }
}
