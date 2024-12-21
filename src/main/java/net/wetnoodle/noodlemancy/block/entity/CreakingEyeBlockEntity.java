package net.wetnoodle.noodlemancy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wetnoodle.noodlemancy.registry.NMBlockEntityTypes;

public class CreakingEyeBlockEntity extends BlockEntity {
    public CreakingEyeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NMBlockEntityTypes.CREAKING_EYE, blockPos, blockState);
    }
}
