package net.wetnoodle.noodlemancy.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.wetnoodle.noodlemancy.registry.NMBlocks;

import java.util.concurrent.CompletableFuture;

public class NMBlockLootProvider extends FabricBlockLootTableProvider {
    public NMBlockLootProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.dropSelf(NMBlocks.CREAKING_EYE);
        this.dropSelf(NMBlocks.SNEEZER);
    }
}
