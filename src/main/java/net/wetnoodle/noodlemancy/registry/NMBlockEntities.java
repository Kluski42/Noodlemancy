package net.wetnoodle.noodlemancy.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.wetnoodle.noodlemancy.NMConstants;
import net.wetnoodle.noodlemancy.block.entity.CreakingEyeBlockEntity;
import net.wetnoodle.noodlemancy.block.entity.SneezerBlockEntity;
import org.jetbrains.annotations.NotNull;

public class NMBlockEntities {
    public static final BlockEntityType<CreakingEyeBlockEntity> CREAKING_EYE = register("creaking_eye",
            FabricBlockEntityTypeBuilder.create(CreakingEyeBlockEntity::new, NMBlocks.CREAKING_EYE).build());

    public static final BlockEntityType<SneezerBlockEntity> SNEEZER = register("sneezer",
            FabricBlockEntityTypeBuilder.create(SneezerBlockEntity::new, NMBlocks.SNEEZER).build());

    @NotNull
    private static <T extends BlockEntityType<?>> T register(@NotNull String path, T blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, NMConstants.id(path), blockEntityType);
    }

    public static void init() {
    }
}
