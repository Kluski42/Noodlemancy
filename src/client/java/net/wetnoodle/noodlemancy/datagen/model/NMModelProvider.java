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
import net.wetnoodle.noodlemancy.block.CreakingEyeBlock;
import net.wetnoodle.noodlemancy.block.SneezerBlock;
import net.wetnoodle.noodlemancy.block.enums.ChargingBlockState;
import net.wetnoodle.noodlemancy.registry.NMBlocks;

public final class NMModelProvider extends FabricModelProvider {
    public NMModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerator) {
        registerCreakingEye(blockModelGenerator, NMBlocks.CREAKING_EYE);
        registerSneezer(blockModelGenerator, NMBlocks.SNEEZER, Blocks.BLAST_FURNACE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }

    public void registerCreakingEye(BlockModelGenerators blockStateModelGenerator, Block block) {
        TextureMapping map0 = createCreakingEyeOffMapping(block);
        ResourceLocation id0 = NMModelTemplates.CREAKING_EYE_OFF_TEMPLATE.create(block, map0, blockStateModelGenerator.modelOutput);
        TextureMapping map1 = createCreakingEyeMapping(block, 1);
        ResourceLocation id1 = NMModelTemplates.CREAKING_EYE_TEMPLATE.createWithSuffix(block, "_1", map1, blockStateModelGenerator.modelOutput);
        TextureMapping map2 = createCreakingEyeMapping(block, 2);
        ResourceLocation id2 = NMModelTemplates.CREAKING_EYE_TEMPLATE.createWithSuffix(block, "_2", map2, blockStateModelGenerator.modelOutput);
        TextureMapping map3 = createCreakingEyeMapping(block, 3);
        ResourceLocation id3 = NMModelTemplates.CREAKING_EYE_TEMPLATE.createWithSuffix(block, "_3", map3, blockStateModelGenerator.modelOutput);

        TextureMapping mapItem = createCreakingEyeOffMapping(block).put(NMTextureSlots.FRONT_EMISSIVE, TextureMapping.getBlockTexture(block, "_emissive_3"));
        ResourceLocation idItem = NMModelTemplates.CREAKING_EYE_TEMPLATE.createWithSuffix(block, "_item", mapItem, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(NMBlocks.CREAKING_EYE, idItem);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(BlockModelGenerators.createFacingDispatch())
                .with(PropertyDispatch.property(CreakingEyeBlock.EYES)
                        .select(0, Variant.variant().with(VariantProperties.MODEL, id0))
                        .select(1, Variant.variant().with(VariantProperties.MODEL, id1))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, id2))
                        .select(3, Variant.variant().with(VariantProperties.MODEL, id3))
                )
        );
    }

    // Make face texture change
    public void registerSneezer(BlockModelGenerators blockStateModelGenerator, Block block, Block stolenBlock) {
        TextureMapping unpoweredMap = createSneezerMapping(block, stolenBlock);
        ResourceLocation unpoweredId = ModelTemplates.CUBE_ORIENTABLE.create(block, unpoweredMap, blockStateModelGenerator.modelOutput);
        TextureMapping chargingMap = createSneezerMapping(block, stolenBlock, "_charging");
        ResourceLocation chargingId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_charging", chargingMap, blockStateModelGenerator.modelOutput);
        TextureMapping holdingMap = createSneezerMapping(block, stolenBlock, "_holding");
        ResourceLocation holdingId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_holding", holdingMap, blockStateModelGenerator.modelOutput);
        TextureMapping triggeredMap = createSneezerMapping(block, stolenBlock, "_triggered");
        ResourceLocation triggeredId = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_triggered", triggeredMap, blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(BlockStateProperties.ORIENTATION)
                        .generate(frontAndTop -> blockStateModelGenerator.applyRotation(frontAndTop, Variant.variant())))
                .with(PropertyDispatch.property(SneezerBlock.CHARGE_STATE)
                        .select(ChargingBlockState.UNPOWERED, Variant.variant().with(VariantProperties.MODEL, unpoweredId))
                        .select(ChargingBlockState.CHARGING, Variant.variant().with(VariantProperties.MODEL, chargingId))
                        .select(ChargingBlockState.HOLDING, Variant.variant().with(VariantProperties.MODEL, holdingId))
                        .select(ChargingBlockState.TRIGGERED, Variant.variant().with(VariantProperties.MODEL, triggeredId))
                )
        );
    }

    // Mappings

    private TextureMapping createCreakingEyeOffMapping(Block block) {
        return new TextureMapping()
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.BACK, TextureMapping.getBlockTexture(block, "_back"));
    }

    private TextureMapping createCreakingEyeMapping(Block block, int eyes) {
        return new TextureMapping()
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.BACK, TextureMapping.getBlockTexture(block, "_back_on"))
                .put(NMTextureSlots.FRONT_EMISSIVE, TextureMapping.getBlockTexture(block, "_emissive_" + eyes));
    }

    private TextureMapping createSneezerMapping(Block block, Block stolenBlock) {
        return createSneezerMapping(block, stolenBlock, "");
    }

    private TextureMapping createSneezerMapping(Block block, Block stolenBlock, String frontSuffix) {
        return new TextureMapping()
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front" + frontSuffix))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(stolenBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(stolenBlock, "_top"));
    }
}
