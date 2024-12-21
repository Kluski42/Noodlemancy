package net.wetnoodle.noodlemancy.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.wetnoodle.noodlemancy.NMConstants;
import net.wetnoodle.noodlemancy.block.CreakingEyeBlock;

import java.util.function.Function;

public class NMBlocks {
    public static final Block CREAKING_EYE = register(
            "creaking_eye",
            CreakingEyeBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CREAKING_HEART)
    );

    public static void init() {
        NMConstants.log("Registering blocks for Noodlemancy", NMConstants.UNSTABLE_LOGGING);
    }

    private static <T extends Block> T registerWithoutItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
        ResourceLocation id = NMConstants.id(path);
        return doRegister(id, makeBlock(block, properties, id));
    }

    private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
        T registered = registerWithoutItem(path, block, properties);
        Items.registerBlock(registered);
        return registered;
    }

    private static <T extends Block> T doRegister(ResourceLocation id, T block) {
        if (BuiltInRegistries.BLOCK.getOptional(id).isEmpty()) {
            return Registry.register(BuiltInRegistries.BLOCK, id, block);
        }
        throw new IllegalArgumentException("Block with id " + id + " is already in the block registry.");
    }

    private static <T extends Block> T makeBlock(Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties, ResourceLocation id) {
        return function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
    }
}
