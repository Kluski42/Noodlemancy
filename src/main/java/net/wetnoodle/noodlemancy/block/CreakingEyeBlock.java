package net.wetnoodle.noodlemancy.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.wetnoodle.noodlemancy.block.entity.CreakingEyeBlockEntity;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreakingEyeBlock extends BaseEntityBlock {
    public static final MapCodec<CreakingEyeBlock> CODEC = RecordCodecBuilder.mapCodec(
            color -> color.group(propertiesCodec()).apply(color, CreakingEyeBlock::new)
    );
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final IntegerProperty EYES = IntegerProperty.create("eyes", 0, 3);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    // Block states and init

    public CreakingEyeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWER, 0).setValue(EYES, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(POWER).add(EYES).add(FACING);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, NMBlockEntities.CREAKING_EYE, CreakingEyeBlockEntity::tick);
    }

    // Fun stuff

    public static void updatePower(Level level, BlockPos blockPos, BlockState blockState, int newPower) {
        if (blockState.getValue(POWER) != newPower) {
            level.setBlock(blockPos, blockState.setValue(POWER, newPower).setValue(EYES, (int) Math.ceil(newPower / 5.0)), Block.UPDATE_ALL);
            CreakingEyeBlock.updateNeighbours(level, blockPos, blockState);
        }
    }

    private static void updateNeighbours(Level level, BlockPos blockPos, BlockState blockState) {
        Block block = blockState.getBlock();
        Direction direction = blockState.getValue(FACING).getOpposite();
        level.updateNeighborsAt(blockPos, block);
        level.updateNeighborsAt(blockPos.relative(direction), block);
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            if (!level.isClientSide && (blockState.getValue(POWER) > 0)) {
                updateNeighbours(level, blockPos, blockState.setValue(POWER, 0));
            }
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getSignal(blockGetter, blockPos, direction);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(FACING) == direction ? blockState.getValue(POWER) : 0;
    }
}
