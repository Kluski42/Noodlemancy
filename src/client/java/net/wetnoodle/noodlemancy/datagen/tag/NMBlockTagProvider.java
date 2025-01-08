package net.wetnoodle.noodlemancy.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NMBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public NMBlockTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NMBlocks.CREAKING_EYE);
    }
}
