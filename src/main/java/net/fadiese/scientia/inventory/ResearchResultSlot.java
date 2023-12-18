package net.fadiese.scientia.inventory;

import net.fadiese.scientia.handler.ScientiaItemStackHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ResearchResultSlot extends SlotItemHandler {

    private final Player player;


    public ResearchResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player pPlayer) {
        super(itemHandler, index, xPosition, yPosition);
        player = pPlayer;
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        this.checkTakeAchievements(pStack);
        super.onTake(pPlayer, pStack);
    }

    protected void checkTakeAchievements(ItemStack pStack) {
        pStack.onCraftedBy(this.player.level, this.player, 1);
        if (this.player instanceof ServerPlayer && this.getItemHandler() instanceof ScientiaItemStackHandler) {
            ((ScientiaItemStackHandler) getItemHandler()).onResultRetrieved((ServerPlayer) player);
        }
    }
}
