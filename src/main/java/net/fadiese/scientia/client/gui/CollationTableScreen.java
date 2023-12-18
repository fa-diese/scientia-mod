package net.fadiese.scientia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.helper.TopicHelper;
import net.fadiese.scientia.inventory.CollationTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollationTableScreen extends AbstractContainerScreen<CollationTableMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Scientia.MOD_ID, "textures/gui/collation_table_gui.png");

    private boolean isHoveredButton = false;
    private boolean playerLevelCost = false;

    private final StringBuilder str = new StringBuilder();
    private final List<Component> topicTooltipHeader = List.of(Component.translatable("gui.topic.tooltip.header"));
    private final List<Component> failureTooltips = new ArrayList<Component>() {
        {
            add(Component.translatable("gui.failure.tooltip.header"));
            add(Component.empty());
            add(Component.empty());
        }
    };
    private final Component booksHeader = Component.translatable("gui.books.header");
    private final Component costHeader = Component.translatable("gui.level_cost.header");
    private final Component chancesHeader = Component.translatable("gui.chances.header");

    private final Component lowChances = Component.translatable("gui.chances.low");
    private final Component mediumLowChances = Component.translatable("gui.chances.medium_low");
    private final Component mediumChances = Component.translatable("gui.chances.medium");
    private final Component mediumHighChances = Component.translatable("gui.chances.medium_high");
    private final Component highChances = Component.translatable("gui.chances.high");

    public CollationTableScreen(CollationTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 186;
        System.out.println(failureTooltips);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        boolean isHoveredUpperTopicTip;
        boolean isHoveredLowerTopicTip;
        boolean isHoveredFailureTip;
        playerLevelCost = this.minecraft.player.getAbilities().instabuild || this.minecraft.player.experienceLevel >= menu.getLevelCost();
        if (isHoveredUpperTopicTip = this.isHovering(37, 31, 7, 10, pMouseX, pMouseY)) {
            isHoveredLowerTopicTip = false;
            isHoveredFailureTip = false;
            isHoveredButton = false;
        } else if (isHoveredLowerTopicTip = this.isHovering(37, 49, 7, 10, pMouseX, pMouseY)) {
            isHoveredFailureTip = false;
            isHoveredButton = false;
        } else if (isHoveredFailureTip = this.isHovering(106, 61, 7, 10, pMouseX, pMouseY)) {
            isHoveredButton = false;
        } else {
            isHoveredButton = (this.isHovering(63, 89, 10, 9, pMouseX, pMouseY) && menu.hasRecipe() && menu.hasRequiredKnowledgeLevel() && playerLevelCost) || (this.isHovering(82, 89, 9, 9, pMouseX, pMouseY) && menu.isRunning());
        }

        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.getLevelCost() > 1) {
            int offset = menu.getLevelCost() < 5 ? (menu.getLevelCost() - 2) * 11 : 22;
            blit(pPoseStack, leftPos + 158, topPos + 23, 189 + offset, 186, 11, 11);
        }

        if (menu.isRunning()) {
            int featherStep = menu.getProgress() % 22 / 2;
            if (featherStep != 0) {
                blit(pPoseStack, leftPos + 156, topPos + 71, 72 + (featherStep - 1) * 10, 186, 10, 26);
            }
            blit(pPoseStack, leftPos + 41, topPos + 78, 0, 186, menu.getScaledProgress(), 6);
            if (isHoveredButton) {
                blit(pPoseStack, leftPos + 82, topPos + 89, 172, 213, 9, 9);
            } else {
                blit(pPoseStack, leftPos + 82, topPos + 89, 172, 204, 9, 9);
            }
        } else if (menu.hasRecipe() && menu.hasRequiredKnowledgeLevel() && playerLevelCost) {
            if (isHoveredButton) {
                blit(pPoseStack, leftPos + 63, topPos + 89, 172, 195, 10, 9);
            } else {
                blit(pPoseStack, leftPos + 63, topPos + 89, 172, 186, 10, 9);
            }
            if (isHoveredFailureTip) {
                blit(pPoseStack, leftPos + 106, topPos + 61, 182, 196, 7, 10);
                failureTooltips.set(1, TopicHelper.getInstance().getTopicComponent(menu.getTopicInSlot(36)));
                failureTooltips.set(2, TopicHelper.getInstance().getTopicComponent(menu.getTopicInSlot(37)));
                this.renderTooltip(pPoseStack, failureTooltips, Optional.empty(), pMouseX, pMouseY);
            } else {
                blit(pPoseStack, leftPos + 106, topPos + 61, 182, 186, 7, 10);
            }
        } else {
            if (menu.getTopicInSlot(36).isEmpty() && TopicHelper.getInstance().isTopicRegistered(menu.getTopicInSlot(37))) {
                if (isHoveredUpperTopicTip) {
                    blit(pPoseStack, leftPos + 37, topPos + 31, 182, 196, 7, 10);
                    this.renderTooltip(pPoseStack, topicTooltipHeader, TopicHelper.getInstance().getCompatibleTopicsTooltip(menu.getTopicInSlot(37)), pMouseX, pMouseY);

                } else {
                    blit(pPoseStack, leftPos + 37, topPos + 31, 182, 186, 7, 10);
                }
            } else if (menu.getTopicInSlot(37).isEmpty() && TopicHelper.getInstance().isTopicRegistered(menu.getTopicInSlot(36))) {
                if (isHoveredLowerTopicTip) {
                    blit(pPoseStack, leftPos + 37, topPos + 49, 182, 196, 7, 10);
                    this.renderTooltip(pPoseStack, topicTooltipHeader, TopicHelper.getInstance().getCompatibleTopicsTooltip(menu.getTopicInSlot(36)), pMouseX, pMouseY);

                } else {
                    blit(pPoseStack, leftPos + 37, topPos + 49, 182, 186, 7, 10);
                }
            }
        }
        if (menu.getChanceCategory() != 0) {
            this.font.draw(pPoseStack, chancesHeader, leftPos + 107 - this.font.width(chancesHeader), topPos + 39, 6381646);
            switch (menu.getChanceCategory()) {
                case 1:
                    this.font.draw(pPoseStack, lowChances, leftPos + 168 - this.font.width(lowChances), topPos + 39, 12543076);
                    break;
                case 2:
                    this.font.draw(pPoseStack, mediumLowChances, leftPos + 168 - this.font.width(mediumLowChances), topPos + 39, 10901127);
                    break;
                case 3:
                    this.font.draw(pPoseStack, mediumChances, leftPos + 168 - this.font.width(mediumChances), topPos + 39, 9852583);
                    break;
                case 4:
                    this.font.draw(pPoseStack, mediumHighChances, leftPos + 168 - this.font.width(mediumHighChances), topPos + 39, 7952063);
                    break;
                case 5:
                    this.font.draw(pPoseStack, highChances, leftPos + 168 - this.font.width(highChances), topPos + 39, 5789403);
                    break;
            }
        } else {
            this.font.draw(pPoseStack, chancesHeader, leftPos + 107 - this.font.width(chancesHeader), topPos + 39, 11711154);
        }
        String knowledgeValues = str.append(menu.getRequiredKnowledgeLevel()).append(" / ").append(menu.getKnowledgeLevel()).toString();
        if (menu.hasRecipe() && !menu.hasRequiredKnowledgeLevel()) {
            this.font.draw(pPoseStack, booksHeader, leftPos + 107 - this.font.width(booksHeader), topPos + 11, 6381646);
            this.font.draw(pPoseStack, knowledgeValues, leftPos + 154 - this.font.width(knowledgeValues), topPos + 11, 10042687);
        } else if (menu.hasRecipe() && menu.hasRequiredKnowledgeLevel()) {
            this.font.draw(pPoseStack, booksHeader, leftPos + 107 - this.font.width(booksHeader), topPos + 11, 6381646);
            this.font.draw(pPoseStack, knowledgeValues, leftPos + 154 - this.font.width(knowledgeValues), topPos + 11, 6381646);
        } else {
            this.font.draw(pPoseStack, booksHeader, leftPos + 107 - this.font.width(booksHeader), topPos + 11, 11711154);
            this.font.draw(pPoseStack, knowledgeValues, leftPos + 154 - this.font.width(knowledgeValues), topPos + 11, 11711154);
        }
        str.setLength(0);
        String levelCost = String.valueOf(menu.getLevelCost());
        if (menu.hasRecipe() && !playerLevelCost) {
            this.font.draw(pPoseStack, costHeader, leftPos + 107 - this.font.width(costHeader), topPos + 25, 6381646);
            this.font.draw(pPoseStack, levelCost, leftPos + 154 - this.font.width(levelCost), topPos + 25, 10042687);
        } else if (menu.hasRecipe() && playerLevelCost) {
            this.font.draw(pPoseStack, costHeader, leftPos + 107 - this.font.width(costHeader), topPos + 25, 6381646);
            this.font.draw(pPoseStack, levelCost, leftPos + 154 - this.font.width(levelCost), topPos + 25, 6381646);
        } else {
            this.font.draw(pPoseStack, costHeader, leftPos + 107 - this.font.width(costHeader), topPos + 25, 11711154);
            this.font.draw(pPoseStack, levelCost, leftPos + 154 - this.font.width(levelCost), topPos + 25, 11711154);
        }
        str.setLength(0);
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
        if (menu.isRunning() && isHoveredButton) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            return true;
        } else if (isHoveredButton) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, pButton);
    }
}
