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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.wetnoodle.noodlemancy.block.PressurizedDropper;
import net.wetnoodle.noodlemancy.block.enums.ChargingBlockState;
import net.wetnoodle.noodlemancy.registry.NMBlocks;

public final class NMModelProvider extends FabricModelProvider {
    public NMModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerator) {
        registerPressurizedDropper(blockModelGenerator, NMBlocks.PRESSURIZED_DROPPER, Blocks.BLAST_FURNACE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }

//    public void registerCreakingEyeItem(ItemModelGenerators itemModelGenerator, Block block) {
//        final ModelTemplate CREAKING_EYE_3 = createBlockModelTemplate("template_fence_gate_open", "_3", TextureSlot.TEXTURE);
//        ResourceLocation vertIdentifier = CREAKING_EYE_3.create(block, TextureMapping.defaultTexture(block), itemModelGenerator.modelOutput);
//    }

    // Make face texture change
    public void registerPressurizedDropper(BlockModelGenerators blockStateModelGenerator, Block block, Block stolenBlock) {
        TextureMapping unpoweredMap = createPressurizedDropperMapping(block, stolenBlock);
        ResourceLocation unpoweredId = ModelTemplates.CUBE_ORIENTABLE.create(block, unpoweredMap, blockStateModelGenerator.modelOutput);
        TextureMapping chargingMap = createPressurizedDropperMapping(block, stolenBlock, "_charging");
        ResourceLocation chargingId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_charging", chargingMap, blockStateModelGenerator.modelOutput);
        TextureMapping holdingMap = createPressurizedDropperMapping(block, stolenBlock, "_holding");
        ResourceLocation holdingId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_holding", holdingMap, blockStateModelGenerator.modelOutput);
        TextureMapping triggeredMap = createPressurizedDropperMapping(block, stolenBlock, "_triggered");
        ResourceLocation triggeredId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_triggered", triggeredMap, blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(BlockStateProperties.ORIENTATION)
                        .generate(frontAndTop -> blockStateModelGenerator.applyRotation(frontAndTop, Variant.variant())))
                .with(PropertyDispatch.property(PressurizedDropper.CHARGE_STATE)
                        .select(ChargingBlockState.UNPOWERED, Variant.variant().with(VariantProperties.MODEL, unpoweredId))
                        .select(ChargingBlockState.CHARGING, Variant.variant().with(VariantProperties.MODEL, chargingId))
                        .select(ChargingBlockState.HOLDING, Variant.variant().with(VariantProperties.MODEL, holdingId))
                        .select(ChargingBlockState.TRIGGERED, Variant.variant().with(VariantProperties.MODEL, triggeredId))
                )
        );
    }

    private TextureMapping createPressurizedDropperMapping(Block block, Block stolenBlock) {
        return createPressurizedDropperMapping(block, stolenBlock, "");
    }

    private TextureMapping createPressurizedDropperMapping(Block block, Block stolenBlock, String frontSuffix) {
        return new TextureMapping()
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front" + frontSuffix))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(stolenBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(stolenBlock, "_top"));
    }
}
