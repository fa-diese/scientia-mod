package net.fadiese.scientia.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClientItemListTooltip implements ClientTooltipComponent {

    private final List<ItemStack> items;

    private final int gridSizeX;

    private final int gridSizeY;

    public ClientItemListTooltip(List<ItemStack> tools) {
        this.items = tools;
        gridSizeX = Math.min(tools.size(), 5);
        gridSizeY = (int) Math.ceil(((double) this.items.size()) / (double) gridSizeX);
    }

    @Override
    public int getHeight() {
        return gridSizeY * 18 + 3;
    }

    @Override
    public int getWidth(@NotNull Font pFont) {
        return gridSizeX * 18 + 2;
    }

    @Override
    public void renderImage(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull PoseStack pPoseStack, @NotNull ItemRenderer pItemRenderer, int pBlitOffset) {
        for (int i = 0; i < items.size(); i++) {
            pItemRenderer.renderAndDecorateFakeItem(items.get(i), pMouseX + (i % gridSizeX) * 18 + 1, pMouseY + (i / gridSizeX) * 18 + 1);
        }
    }
}
