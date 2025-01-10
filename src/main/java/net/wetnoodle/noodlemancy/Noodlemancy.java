package net.wetnoodle.noodlemancy;

import net.fabricmc.api.ModInitializer;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import net.wetnoodle.noodlemancy.registry.NMInventorySorting;

public class Noodlemancy implements ModInitializer {
    @Override
    public void onInitialize() {
        NMBlocks.init();
        NMInventorySorting.init();
        NMBlockEntities.init();
    }
}