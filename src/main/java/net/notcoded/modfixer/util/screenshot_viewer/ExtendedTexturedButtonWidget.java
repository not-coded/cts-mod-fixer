package net.notcoded.modfixer.util.screenshot_viewer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ExtendedTexturedButtonWidget extends ImageButton implements CustomHoverState {
    private final @Nullable ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset;
    private final int textureWidth;
    private final int textureHeight;

    ExtendedTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, @Nullable ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress pressAction, Component text) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, text);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    public @Nullable ResourceLocation getTexture() {
        return this.texture;
    }

    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        ResourceLocation texture = this.getTexture();
        if (texture == null) {
            GuiComponent.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, 16777215);
        } else {
            Minecraft.getInstance().getTextureManager().bind(texture);
            int vOffset = this.v;
            if (this.isHovered()) {
                vOffset += this.hoveredVOffset;
            }

            RenderSystem.enableDepthTest();
            GuiComponent.blit(matrices, this.x, this.y, (float)this.u, (float)vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
            if (this.isHovered) {
                this.renderToolTip(matrices, mouseX, mouseY);
            }
        }

    }

    public void updateHoveredState(int mouseX, int mouseY) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
