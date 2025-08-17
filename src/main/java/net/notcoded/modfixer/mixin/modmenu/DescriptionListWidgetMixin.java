package net.notcoded.modfixer.mixin.modmenu;

import com.terraformersmc.modmenu.gui.widget.DescriptionListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DescriptionListWidget.class)
public abstract class DescriptionListWidgetMixin {

    /**
     * Due to net.minecraft.client.gui.components.AbstractSelectionList.getMaxScroll() being private,
     * we have to redirect it to our own method which is just copied and pasted from the original.
     * <p>
     * This is because Mod Menu does not have an AW entry for that method and crashes with an IllegalAccessError.
     */

    @Redirect(method = "renderScrollBar", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/modmenu/gui/widget/DescriptionListWidget;getMaxScroll()I"))
    private int fixGetMaxScroll(DescriptionListWidget descriptionListWidget) {
        return this.getMaxScroll(descriptionListWidget);
    }

    @Unique
    public final int getMaxScroll(DescriptionListWidget descriptionListWidget) {
        return Math.max(0, descriptionListWidget.getMaxPosition() - (descriptionListWidget.y1 - descriptionListWidget.y0 - 4));
    }
}
