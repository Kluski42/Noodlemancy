package net.wetnoodle.noodlemancy.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.wetnoodle.noodlemancy.NMConstants;
import net.wetnoodle.noodlemancy.block.dispenser.StackDispenserBehavior;
import net.wetnoodle.noodlemancy.block.entity.PressurizedDropperBlockEntity;
import net.wetnoodle.noodlemancy.block.enums.ChargingBlockState;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.wetnoodle.noodlemancy.block.enums.ChargingBlockState.*;

public class PressurizedDropper extends BaseEntityBlock {
    public static final MapCodec<PressurizedDropper> CODEC = BlockBehaviour.simpleCodec(PressurizedDropper::new);
    public static final EnumProperty<ChargingBlockState> CHARGE_STATE = EnumProperty.create("state", ChargingBlockState.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
//    public static final BooleanProperty CHARGING = BooleanProperty.of("charging");

        private static final DispenseItemBehavior BEHAVIOR = new StackDispenserBehavior();
    private static final int CHARGING_TIME = 20;

    public PressurizedDropper(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CHARGE_STATE, UNPOWERED));
    }

    // Registering Properties

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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

    // Block Entity / Dropper Stuff

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PressurizedDropperBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, NMBlockEntities.PRESSURIZED_DROPPER, PressurizedDropperBlockEntity::tick);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, bl);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof PressurizedDropperBlockEntity blockEntity) {
            player.openMenu(blockEntity);
            // ToDo: Add Stats
        }
        return InteractionResult.CONSUME;
    }

    // Unique Functionality


    @Override
    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof PressurizedDropperBlockEntity pressurizedDropperBlockEntity) {
            return pressurizedDropperBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean bl) {
        if (!(level.getBlockEntity(pos) instanceof PressurizedDropperBlockEntity blockEntity)) return;
        boolean isReceivingPower = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        ChargingBlockState chargeState = state.getValue(CHARGE_STATE);
        if (isReceivingPower && chargeState.equals(UNPOWERED)) {
            level.scheduleTick(pos, this, CHARGING_TIME);
            level.setBlock(pos, state.setValue(CHARGE_STATE, CHARGING), Block.UPDATE_CLIENTS);
            blockEntity.updatedTime = level.getGameTime();
        } else if (!isReceivingPower && chargeState.equals(CHARGING)) {
            level.setBlock(pos, state.setValue(CHARGE_STATE, UNPOWERED), Block.UPDATE_CLIENTS);
        } else if (!isReceivingPower && chargeState.equals(TRIGGERED)) {
            level.scheduleTick(pos, this, 4);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        boolean isReceivingPower = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        ChargingBlockState chargeState = state.getValue(CHARGE_STATE);
        if (!(level.getBlockEntity(pos) instanceof PressurizedDropperBlockEntity blockEntity)) return;
        if (chargeState.equals(CHARGING) && isReceivingPower) {
            int timeDif = (int) (level.getGameTime() - blockEntity.updatedTime);
            if (timeDif >= CHARGING_TIME) {
                level.setBlock(pos, state.setValue(CHARGE_STATE, TRIGGERED), Block.UPDATE_CLIENTS);
                dispense(level, state, pos);
            } else {
                level.scheduleTick(pos, this, timeDif);
            }
        } else if (chargeState.equals(TRIGGERED) && !isReceivingPower) {
            level.setBlock(pos, state.setValue(CHARGE_STATE, UNPOWERED), Block.UPDATE_CLIENTS);
        }
    }

    protected void dispense(ServerLevel level, BlockState state, BlockPos pos) {
        PressurizedDropperBlockEntity pressurizedDropperEntity = level.getBlockEntity(pos, NMBlockEntities.PRESSURIZED_DROPPER).orElse(null);
        if (pressurizedDropperEntity == null) {
            NMConstants.warn("Ignoring dispensing attempt for Power Dropper without matching block entity at " + pos, true);
            return;
        }
        BlockSource blockPointer = new BlockSource(level, pos, state, pressurizedDropperEntity);
        int slot = pressurizedDropperEntity.getRandomSlot(level.random);
        if (slot < 0) {
            level.levelEvent(1001, pos, 0);
        } else {
            ItemStack itemStack = pressurizedDropperEntity.getItem(slot);
            if (!itemStack.isEmpty()) {
                Direction direction = level.getBlockState(pos).getValue(FACING);
                Container targetInventory = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
                ItemStack remainingStack;
                if (targetInventory == null) {
                    remainingStack = BEHAVIOR.dispense(blockPointer, itemStack);
                } else {
                    // Bug: This deletes the stack if the target can't receive the items
                    remainingStack = HopperBlockEntity.addItem(pressurizedDropperEntity, targetInventory, itemStack, direction.getOpposite());
                    if (remainingStack.isEmpty()) {
                        remainingStack = itemStack.copy();
                        remainingStack.setCount(0);
                    } else {
                        remainingStack = itemStack.copy();
                    }
                }
                pressurizedDropperEntity.setItem(slot, remainingStack);
            }
        }
    }
}
