package net.wetnoodle.noodlemancy.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.wetnoodle.noodlemancy.NMConstants;
import net.wetnoodle.noodlemancy.block.entity.PressurizedDropperBlockEntity;
import net.wetnoodle.noodlemancy.block.enums.ChargingBlockState;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.wetnoodle.noodlemancy.block.enums.ChargingBlockState.*;

public class PressurizedDropper extends BaseEntityBlock {
    public static final MapCodec<PressurizedDropper> CODEC = BlockBehaviour.simpleCodec(PressurizedDropper::new);
    public static final EnumProperty<ChargingBlockState> CHARGE_STATE = EnumProperty.create("state", ChargingBlockState.class);
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final int CHARGING_TIME = 20;

    public PressurizedDropper(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP).setValue(CHARGE_STATE, UNPOWERED).setValue(POWERED, false));
    }

    // Registering Properties

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Direction direction = blockPlaceContext.getNearestLookingDirection().getOpposite();
        Direction direction2 = switch (direction) {
            case DOWN -> blockPlaceContext.getHorizontalDirection().getOpposite();
            case UP -> blockPlaceContext.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };
        return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction2));
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(ORIENTATION, rotation.rotation().rotate(blockState.getValue(ORIENTATION)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(ORIENTATION, mirror.rotation().rotate(blockState.getValue(ORIENTATION)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, CHARGE_STATE, POWERED);
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
        boolean stateUpdated = false;
        boolean receivingPower = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        if (receivingPower != state.getValue(POWERED)) {
            state = state.setValue(POWERED, receivingPower);
            stateUpdated = true;
        }
        ChargingBlockState chargeState = state.getValue(CHARGE_STATE);
        if (chargeState.equals(UNPOWERED) && receivingPower) {
            level.scheduleTick(pos, this, CHARGING_TIME);
            state = state.setValue(CHARGE_STATE, CHARGING);
            stateUpdated = true;
            blockEntity.updatedTime = level.getGameTime();
        } else if (chargeState.equals(CHARGING) && !receivingPower) {
            state = state.setValue(CHARGE_STATE, UNPOWERED);
            stateUpdated = true;
        } else if (chargeState.equals(TRIGGERED) && !receivingPower) {
            level.scheduleTick(pos, this, 4);
        }
        if (stateUpdated) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        boolean receivingPower = state.getValue(POWERED);
        ChargingBlockState chargeState = state.getValue(CHARGE_STATE);
        if (!(level.getBlockEntity(pos) instanceof PressurizedDropperBlockEntity blockEntity)) return;
        if (chargeState.equals(CHARGING) && receivingPower) {
            int timeDif = (int) (level.getGameTime() - blockEntity.updatedTime);
            if (timeDif >= CHARGING_TIME) {
                level.setBlock(pos, state.setValue(CHARGE_STATE, HOLDING), Block.UPDATE_CLIENTS);
                level.scheduleTick(pos, this, 4);
            } else {
                level.scheduleTick(pos, this, timeDif);
            }
        } else if (chargeState.equals(HOLDING)) {
            level.setBlock(pos, state.setValue(CHARGE_STATE, TRIGGERED), Block.UPDATE_CLIENTS);
            dispense(level, state, pos);
            if (!receivingPower) level.scheduleTick(pos, this, 4);
        } else if (chargeState.equals(TRIGGERED) && !receivingPower) {
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
                Direction direction = level.getBlockState(pos).getValue(ORIENTATION).front();
                Container targetInventory = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
                ItemStack remainingStack;
                if (targetInventory == null) {
                    remainingStack = dispenseItems(blockPointer, itemStack);
                } else {
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

    private ItemStack dispenseItems(BlockSource blockSource, ItemStack itemStack) {
        ItemStack itemStack2 = this.execute(blockSource, itemStack);
        this.playSound(blockSource);
        this.playAnimation(blockSource, blockSource.state().getValue(PressurizedDropper.ORIENTATION).front());
        return itemStack2;
    }

    private void playSound(BlockSource blockSource) {
        blockSource.level().levelEvent(1000, blockSource.pos(), 0);
    }

    protected void playAnimation(BlockSource blockSource, Direction direction) {
        blockSource.level().levelEvent(2000, blockSource.pos(), direction.get3DDataValue());
    }

    protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
        Direction direction = blockSource.state().getValue(PressurizedDropper.ORIENTATION).front();
        Position position = PressurizedDropper.getDispensePosition(blockSource);
        ItemStack itemStack = stack.copyAndClear();
        spawnItem(blockSource.level(), itemStack, 6, direction, position);
        return stack;
    }

    public static void spawnItem(Level level, ItemStack stack, int i, Direction direction, Position position) {
        double xPos = position.x();
        double yPos = position.y();
        double zPos = position.z();
        if (direction.getAxis() == Direction.Axis.Y) {
            yPos -= 0.125;
        } else {
            yPos -= 0.15625;
        }

        ItemEntity itemEntity = new ItemEntity(level, xPos, yPos, zPos, stack);
        double g = level.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(
                level.random.triangle((double) direction.getStepX() * g, 0.0172275 * (double) i),
                level.random.triangle(0.2, 0.0172275 * (double) i),
                level.random.triangle((double) direction.getStepZ() * g, 0.0172275 * (double) i)
        );
        itemEntity.addDeltaMovement(direction.getUnitVec3().scale(
                level.random.triangle(1, 0.0172275 * 3)
        ));
        level.addFreshEntity(itemEntity);
    }


    public static Position getDispensePosition(BlockSource blockSource) {
        return getDispensePosition(blockSource, 0.7, Vec3.ZERO);
    }

    public static Position getDispensePosition(BlockSource blockSource, double d, Vec3 vec3) {
        Direction direction = blockSource.state().getValue(ORIENTATION).front();
        return blockSource.center()
                .add(d * (double) direction.getStepX() + vec3.x(), d * (double) direction.getStepY() + vec3.y(), d * (double) direction.getStepZ() + vec3.z());
    }
}
