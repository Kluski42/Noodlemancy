package net.wetnoodle.noodlemancy.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.wetnoodle.noodlemancy.block.enums.ChargingBlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.wetnoodle.noodlemancy.block.enums.ChargingBlockState.UNPOWERED;

public class PressurizedDropper extends Block {
    public static final MapCodec<PressurizedDropper> CODEC = BlockBehaviour.simpleCodec(PressurizedDropper::new);
    public static final EnumProperty<ChargingBlockState> CHARGE_STATE = EnumProperty.create("state", ChargingBlockState.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
//    public static final BooleanProperty CHARGING = BooleanProperty.of("charging");

    //    private static final DispenseItemBehavior BEHAVIOR = new StackDispenserBehavior();
    private static final int CHARGING_TIME = 20;

    public PressurizedDropper(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CHARGE_STATE, UNPOWERED));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHARGE_STATE);
    }

}
