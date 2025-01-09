package net.wetnoodle.noodlemancy.datagen.model;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.wetnoodle.noodlemancy.registry.NMBlocks;

public final class NMModelProvider extends FabricModelProvider {
    public NMModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerator) {
        registerDispenserLikeOrientable(blockModelGenerator, NMBlocks.POWER_DROPPER, Blocks.BLAST_FURNACE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }

    public final void registerDispenserLikeOrientable(BlockModelGenerators blockStateModelGenerator, Block block, Block stolenBlock) {
        TextureMapping textureMap = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(stolenBlock, "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(stolenBlock, "_side"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"));
        TextureMapping vertTextureMap = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(stolenBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front_vertical"));

        ResourceLocation identifier = ModelTemplates.CUBE_ORIENTABLE.create(block, textureMap, blockStateModelGenerator.modelOutput);
        ResourceLocation vertIdentifier = ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(block, vertTextureMap, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(BlockStateProperties.FACING)
                        .select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, vertIdentifier).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.UP, Variant.variant().with(VariantProperties.MODEL, vertIdentifier))
                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, identifier))
                        .select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                ));
    }
}
