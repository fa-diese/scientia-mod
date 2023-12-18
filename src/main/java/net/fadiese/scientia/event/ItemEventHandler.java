package net.fadiese.scientia.event;

import net.darkhax.gamestages.GameStageHelper;
import net.fadiese.scientia.Scientia;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Scientia.MOD_ID)
public class ItemEventHandler {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntity().getLevel().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            String stage = event.getItemStack().hasTag() ? event.getItemStack().getTag().getString("scientia.gamestage") : "";
            if (!stage.isEmpty()) {
                if (!GameStageHelper.hasStage(player, stage)) {
                    GameStageHelper.addStage(player, stage);
                    GameStageHelper.syncPlayer(player);
                }
            }
        }
    }

}
