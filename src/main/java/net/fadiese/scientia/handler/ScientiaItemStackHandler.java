package net.fadiese.scientia.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.items.ItemStackHandler;

public class ScientiaItemStackHandler extends ItemStackHandler {

    public ScientiaItemStackHandler(int size) {
        super(size);
    }

    public void onResultRetrieved(ServerPlayer player) {
    }
}
