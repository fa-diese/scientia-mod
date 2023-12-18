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

public class ClientTopicTooltip implements ClientTooltipComponent {

    public static final ResourceLocation RESEARCH_ICON = new ResourceLocation(Scientia.MOD_ID, "textures/gui/research_tooltip_icon.png");
    private final Component topic;

    private final Component prefix = Component.translatable("gui.topic.tooltip");

    public ClientTopicTooltip(Component topic) {
        this.topic = topic;
    }

    @Override
    public int getHeight() {
        return 13;
    }

    @Override
    public int getWidth(Font pFont) {
        return pFont.width(prefix) + pFont.width(topic) + 13;
    }

    @Override
    public void renderImage(Font pFont, int pMouseX, int pMouseY, @NotNull PoseStack pPoseStack, @NotNull ItemRenderer pItemRenderer, int pBlitOffset) {
        RenderSystem.setShaderTexture(0, RESEARCH_ICON);
        GuiComponent.blit(pPoseStack, pMouseX, pMouseY, 0, 0, 11, 11, 11, 11);

    }

    @Override
    public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, MultiBufferSource.BufferSource pBufferSource) {
        pFont.drawInBatch(prefix, (float) pX + 13, (float) pY + 2, -1, false, pMatrix4f, pBufferSource, false, 0, 15728880);
        pFont.drawInBatch(topic, (float) pX + 13 + pFont.width(prefix), (float) pY + 2, -1, false, pMatrix4f, pBufferSource, false, 0, 15728880);
    }
}
