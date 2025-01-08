package net.wetnoodle.noodlemancy;

import net.fabricmc.api.ModInitializer;
import net.wetnoodle.noodlemancy.registry.NMBlockEntityTypes;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import net.wetnoodle.noodlemancy.registry.NMInventorySorting;

public class Noodlemancy implements ModInitializer {
    @Override
    public void onInitialize() {
        NMBlocks.init();
        NMInventorySorting.init();
        NMBlockEntityTypes.init();
    }
}