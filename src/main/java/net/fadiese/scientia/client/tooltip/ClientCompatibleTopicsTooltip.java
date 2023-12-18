package net.fadiese.scientia.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.fadiese.scientia.Scientia;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClientCompatibleTopicsTooltip implements ClientTooltipComponent {

    public static final ResourceLocation RESEARCH_ICON = new ResourceLocation(Scientia.MOD_ID, "textures/gui/research_tooltip_icon.png");
    private final List<Component> compatibleTopics;

    public ClientCompatibleTopicsTooltip(List<Component> compatibleTopics) {
        this.compatibleTopics = compatibleTopics;
    }

    @Override
    public int getHeight() {
        return 13 * compatibleTopics.size();
    }

    @Override
    public int getWidth(@NotNull Font pFont) {
        return compatibleTopics.stream().mapToInt(elt -> pFont.width(elt) + 13).max().orElse(0);
    }

    @Override
    public void renderText(@NotNull Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f, MultiBufferSource.@NotNull BufferSource pBufferSource) {
        for (int i = 0; i < compatibleTopics.size(); i++) {
            pFont.drawInBatch(compatibleTopics.get(i), (float) pX + 13, (float) pY + 2 + i * 13, -1, false, pMatrix4f, pBufferSource, false, 0, 15728880);
        }
    }

    @Override
    public void renderImage(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull PoseStack pPoseStack, @NotNull ItemRenderer pItemRenderer, int pBlitOffset) {
        RenderSystem.setShaderTexture(0, RESEARCH_ICON);
        for (int i = 0; i < compatibleTopics.size(); i++) {
            GuiComponent.blit(pPoseStack, pMouseX, pMouseY + i * 13, 0, 0, 11, 11, 11, 11);
        }
    }
}
