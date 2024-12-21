package net.wetnoodle.noodlemancy.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.wetnoodle.noodlemancy.block.entity.CreakingEyeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CreakingEyeBlock extends BaseEntityBlock {
    public static final MapCodec<CreakingEyeBlock> CODEC = RecordCodecBuilder.mapCodec(
            color -> color.group(propertiesCodec()).apply(color, CreakingEyeBlock::new)
    );
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public CreakingEyeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CreakingEyeBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
}
