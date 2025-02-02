package net.wetnoodle.noodlemancy.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import net.wetnoodle.noodlemancy.registry.tag.NMBlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NMBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public NMBlockTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        generateMiningTags();
        generateSneezerTags();
    }

    private void generateMiningTags() {
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NMBlocks.CREAKING_EYE)
                .add(NMBlocks.SNEEZER);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    // Stolen directly from Wilder Wild's geyser <3
    private void generateSneezerTags() {
        this.getOrCreateTagBuilder(NMBlockTags.AIR_CAN_PASS_THROUGH)
                .addOptionalTag(BlockTags.TRAPDOORS)
                .add(Blocks.COPPER_GRATE)
                .add(Blocks.EXPOSED_COPPER_GRATE)
                .add(Blocks.WEATHERED_COPPER_GRATE)
                .add(Blocks.OXIDIZED_COPPER_GRATE)
                .add(Blocks.WAXED_COPPER_GRATE)
                .add(Blocks.WAXED_EXPOSED_COPPER_GRATE)
                .add(Blocks.WAXED_WEATHERED_COPPER_GRATE)
                .add(Blocks.WAXED_OXIDIZED_COPPER_GRATE)
                .add(Blocks.IRON_BARS);

        this.getOrCreateTagBuilder(NMBlockTags.AIR_CANNOT_PASS_THROUGH)
                .addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS);
    }
}
