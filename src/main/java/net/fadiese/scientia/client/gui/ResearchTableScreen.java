package net.fadiese.scientia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.helper.ResearchToolsHelper;
import net.fadiese.scientia.inventory.ResearchTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Scientia.MOD_ID, "textures/gui/research_table_gui.png");

    private final List<Component> toolsTooltipHeader = List.of(Component.translatable("gui.tools.tooltip.header"));

    private final List<Component> noAdditiveTooltipHeader = List.of(Component.translatable("gui.no_additive.tooltip.header"));

    private final List<Component> additivesTooltipHeader = List.of(Component.translatable("gui.additives.tooltip.header"));

    private boolean isHoveringButton;

    public ResearchTableScreen(ResearchTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 204;
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.hasIncompleteResearchNote()) {
            renderAdvancedRecipeSide(pPoseStack, pPartialTick, pMouseX, pMouseY);
        } else {
            renderSimpleRecipeSide(pPoseStack, pPartialTick, pMouseX, pMouseY);
        }
    }

    private void renderAdvancedRecipeSide(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        blit(pPoseStack, leftPos + 90, topPos + 9, 176, 0, 63, 109);
        boolean isHoveringToolTooltip;
        boolean isHoveringAdditivesTooltip;
        if (isHoveringToolTooltip = this.isHovering(118, 65, 7, 10, pMouseX, pMouseY)) {
            isHoveringButton = false;
            isHoveringAdditivesTooltip = false;
        } else if (isHoveringAdditivesTooltip = this.isHovering(118, 83, 7, 10, pMouseX, pMouseY)) {
            isHoveringButton = false;
        } else {
            isHoveringButton = menu.hasRecipe() && this.isHovering(107, 109, 10, 9, pMouseX, pMouseY)
                    || menu.isRunning() && this.isHovering(125, 109, 9, 9, pMouseX, pMouseY);
        }
        if (menu.isRunning()) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            if ((progress % 8) / 2 == 1) {
                blit(pPoseStack, leftPos + 135, topPos + 91, 53, 204, 18, 11);
            } else if ((progress % 8) / 2 == 3) {
                blit(pPoseStack, leftPos + 135, topPos + 91, 53, 215, 18, 11);
            }
            if (progress < (double) maxProgress / 3) {
                blit(pPoseStack, leftPos + 117, topPos + 49, 71, 204, (int) (21 * ((double) progress / ((double) maxProgress / 3))), 1);
            } else if (progress < (2 * (double) maxProgress) / 3) {
                blit(pPoseStack, leftPos + 117, topPos + 49, 71, 204, 21, 1);
                blit(pPoseStack, leftPos + 138, topPos + 55 - (int) (6 * (((double) progress / ((double) maxProgress / 3)) - 1)), 92, (int) (211 - 6 * (((double) progress / ((double) maxProgress / 3)) - 1)), 7, (int) (6 * (((double) progress / ((double) maxProgress / 3)) - 1)));
            } else {
                blit(pPoseStack, leftPos + 117, topPos + 49, 71, 204, 28, 6);
                blit(pPoseStack, leftPos + 141, topPos + 55, 95, 210, 1, (int) (5 * (((double) progress / ((double) maxProgress / 3)) - 2)));
            }
            if (isHoveringButton) {
                blit(pPoseStack, leftPos + 125, topPos + 109, 0, 231, 9, 9);
            } else {
                blit(pPoseStack, leftPos + 125, topPos + 109, 0, 222, 9, 9);
            }
        } else if (menu.hasRecipe()) {
            if (isHoveringButton) {
                blit(pPoseStack, leftPos + 107, topPos + 109, 0, 213, 10, 9);
            } else {
                blit(pPoseStack, leftPos + 107, topPos + 109, 0, 204, 10, 9);
            }
        }
        if (menu.hasTool()) {
            if (isHoveringAdditivesTooltip) {
                blit(pPoseStack, leftPos + 118, topPos + 83, 10, 214, 7, 10);
                Optional<TooltipComponent> tooltip = ResearchToolsHelper.getInstance().getAdditives(menu.slots.get(41).getItem().getItem());
                if (tooltip.isPresent()) {
                    renderTooltip(pPoseStack, additivesTooltipHeader, tooltip, pMouseX, pMouseY);
                } else {
                    renderTooltip(pPoseStack, noAdditiveTooltipHeader, tooltip, pMouseX, pMouseY);
                }
            } else {
                blit(pPoseStack, leftPos + 118, topPos + 83, 10, 204, 7, 10);
            }
        }
        if (isHoveringToolTooltip) {
            blit(pPoseStack, leftPos + 118, topPos + 65, 10, 214, 7, 10);
            renderTooltip(pPoseStack, toolsTooltipHeader, ResearchToolsHelper.getInstance().getResearchTools(), pMouseX, pMouseY);
        }
    }

    private void renderSimpleRecipeSide(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        blit(pPoseStack, leftPos + 25, topPos + 9, 176, 109, 72, 109);
        boolean isHoveringToolTooltip;
        boolean isHoveringAdditivesTooltip;
        if (isHoveringToolTooltip = this.isHovering(64, 65, 7, 10, pMouseX, pMouseY)) {
            isHoveringButton = false;
            isHoveringAdditivesTooltip = false;
        } else if (isHoveringAdditivesTooltip = this.isHovering(64, 83, 7, 10, pMouseX, pMouseY)) {
            isHoveringButton = false;
        } else {
            isHoveringButton = menu.hasRecipe() && this.isHovering(42, 109, 10, 9, pMouseX, pMouseY)
                    || menu.isRunning() && this.isHovering(60, 109, 9, 9, pMouseX, pMouseY);
        }
        if (menu.isRunning()) {
            int bubbleSize = menu.getProgress() % 11 / 2;
            double ratioProgressBar = (double) menu.getProgress() / menu.getMaxProgress();
            blit(pPoseStack, leftPos + 32, topPos + 86 - bubbleSize * 5, 17, 229 - bubbleSize * 5, 5, 12 + bubbleSize * 5);
            blit(pPoseStack, leftPos + 63, topPos + 31, 22, 204, (int) (31 * ratioProgressBar), (int) (16 * ratioProgressBar));
            if (isHoveringButton) {
                blit(pPoseStack, leftPos + 60, topPos + 109, 0, 231, 9, 9);
            } else {
                blit(pPoseStack, leftPos + 60, topPos + 109, 0, 222, 9, 9);
            }
        } else if (menu.hasRecipe()) {
            if (isHoveringButton) {
                blit(pPoseStack, leftPos + 42, topPos + 109, 0, 213, 10, 9);
            } else {
                blit(pPoseStack, leftPos + 42, topPos + 109, 0, 204, 10, 9);
            }
        }
        if (menu.hasTool()) {
            if (isHoveringAdditivesTooltip) {
                blit(pPoseStack, leftPos + 64, topPos + 83, 10, 214, 7, 10);
                Optional<TooltipComponent> tooltip = ResearchToolsHelper.getInstance().getAdditives(menu.slots.get(38).getItem().getItem());
                if (tooltip.isPresent()) {
                    renderTooltip(pPoseStack, additivesTooltipHeader, tooltip, pMouseX, pMouseY);
                } else {
                    renderTooltip(pPoseStack, noAdditiveTooltipHeader, tooltip, pMouseX, pMouseY);
                }
            } else {
                blit(pPoseStack, leftPos + 64, topPos + 83, 10, 204, 7, 10);
            }
        }
        if (isHoveringToolTooltip) {
            blit(pPoseStack, leftPos + 64, topPos + 65, 10, 214, 7, 10);
            renderTooltip(pPoseStack, toolsTooltipHeader, ResearchToolsHelper.getInstance().getResearchTools(), pMouseX, pMouseY);
        }
    }


    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        if (menu.isRunning() && isHoveringButton) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            return true;
        } else if (isHoveringButton) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, pButton);
    }
}
