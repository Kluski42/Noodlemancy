package net.wetnoodle.noodlemancy.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.wetnoodle.noodlemancy.block.SneezerBlock;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import net.wetnoodle.noodlemancy.registry.tag.NMBlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.wetnoodle.noodlemancy.block.SneezerBlock.CHARGE_STATE;
import static net.wetnoodle.noodlemancy.block.enums.ChargingBlockState.CHARGING;
import static net.wetnoodle.noodlemancy.block.enums.ChargingBlockState.HOLDING;

public class SneezerBlockEntity extends DispenserBlockEntity {
    public SneezerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NMBlockEntities.SNEEZER, blockPos, blockState);
    }

    private static final int SUCK_DISTANCE = 3;
    public long updatedTime;
    public int ticks = 0;

    @Override
    public @NotNull Component getDefaultName() {
        return Component.translatable("container.noodlemancy.sneezer");
    }

    // Fun Stuff

    public static void tick(Level level, BlockPos pos, BlockState state, SneezerBlockEntity blockEntity) {
        if (!state.is(NMBlocks.SNEEZER) || blockEntity == null) {
            return;
        }
        if (state.getValue(CHARGE_STATE).equals(CHARGING)) {
            blockEntity.ticks++;
            blockEntity.vacuum(level, pos, state, state.getValue(SneezerBlock.ORIENTATION).front());
            if (blockEntity.ticks >= 20) {
                level.setBlock(pos, state.setValue(CHARGE_STATE, HOLDING), Block.UPDATE_CLIENTS);
                level.scheduleTick(pos, state.getBlock(), 4);
            }
        } else {
            blockEntity.ticks = 0;
        }
    }

    private void vacuum(Level level, BlockPos pos, BlockState state, Direction facing) {
        if (!canSuckThrough(level, pos.relative(facing), level.getBlockState(pos.relative(facing)), facing) || HopperBlockEntity.getContainerAt(level, pos.relative(facing)) != null)
            return;
        Vec3 facePos = pos.getCenter().relative(facing, 0.5);
        // Windbox
        succ(level, pos, state, facing, facePos);
        // Item collection
        if (!level.isClientSide() && (ticks % 2 == 1)) collectItems(level, facing, facePos);
    }

    private void succ(Level level, BlockPos pos, BlockState state, Direction facing, Vec3 facePos) {
        BlockPos endPos = suckEndPos(level, pos, facing);
        List<ItemEntity> entities = getTargets(level, pos, facing, endPos);
        for (ItemEntity entity : entities) {
            Vec3 entityPos = entity.position();
            Vec3 distance = facePos.subtract(entityPos);
            entity.addDeltaMovement(facing.getOpposite().getUnitVec3().scale(0.15 / distance.length()));
        }
        if (level.isClientSide())
            spawnActiveParticles(level, endPos.getCenter(), pos.getCenter(), facing, level.random);
    }

    // Currently, this extends one block further than it should if stopped early. Should it be kept?
    private List<ItemEntity> getTargets(Level level, BlockPos pos, Direction direction, BlockPos endPos) {
        AABB windBox = AABB.encapsulatingFullBlocks(pos.relative(direction), endPos);
        return level.getEntitiesOfClass(ItemEntity.class, windBox, EntitySelector.ENTITY_STILL_ALIVE);
    }

    private BlockPos suckEndPos(Level level, BlockPos pos, Direction direction) {
        BlockPos.MutableBlockPos mutablePos = pos.relative(direction).mutable();
        for (int i = 2; i <= SUCK_DISTANCE; i++) {
            mutablePos.move(direction);
            BlockState state = level.getBlockState(mutablePos);
            if (!canSuckThrough(level, mutablePos, state, direction)) break;
        }
        return mutablePos;
    }

    public static boolean canSuckThrough(Level level, BlockPos pos, BlockState state, Direction direction) {
        return (!state.isFaceSturdy(level, pos, direction.getOpposite(), SupportType.CENTER)
                || state.is(NMBlockTags.AIR_CAN_PASS_THROUGH))
                && !state.is(NMBlockTags.AIR_CANNOT_PASS_THROUGH);
    }

    private void collectItems(Level level, Direction facing, Vec3 facePos) {
        Vec3 start = facePos;
        Vec3 end = facePos;
        Vec3 modVec = Vec3.ZERO;
        if (!facing.getAxis().equals(Direction.Axis.X)) modVec = modVec.add(0.5, 0, 0);
        if (!facing.getAxis().equals(Direction.Axis.Y)) modVec = modVec.add(0, 0.5, 0);
        if (!facing.getAxis().equals(Direction.Axis.Z)) modVec = modVec.add(0, 0, 0.5);
        start = start.subtract(modVec);
        end = end.add(modVec).add(facing.getUnitVec3().scale(0.1));
        AABB collectionBox = new AABB(start, end);
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, collectionBox, EntitySelector.ENTITY_STILL_ALIVE);
        for (ItemEntity itemEntity : itemEntities) {
            if (!HopperBlockEntity.addItem(this, itemEntity)) continue;
            return;
        }
    }

    public int getComparatorOutput() {
        int i = 0;
        for (ItemStack itemStack : this.getItems()) {
            if (!itemStack.isEmpty()) i++;
        }
        return i;
    }

    @Environment(EnvType.CLIENT)
    public static void spawnActiveParticles(Level level, Vec3 startPos, Vec3 ejectorPos, Direction direction, RandomSource random) {
        Vec3 originPos = randomizeParticlePos(startPos, 1, random);
        Vec3 particleVel = getParticleVelocity(originPos, ejectorPos, random, 0.4, .7);
        level.addParticle(
                ParticleTypes.DUST_PLUME,
                originPos.x, originPos.y, originPos.z,
                particleVel.x, particleVel.y, particleVel.z
        );
    }

    private static Vec3 randomizeParticlePos(Vec3 pos, double diameter, RandomSource random) {
        return pos.add(
                diameter * (random.nextDouble() - 0.5),
                diameter * (random.nextDouble() - 0.5),
                diameter * (random.nextDouble() - 0.5)
        );
    }

    private static Vec3 getParticleVelocity(Vec3 startPos, Vec3 ejectorPos, RandomSource random, double min, double max) {
        double difference = max - min;
        double velocity = min + (random.nextDouble() * difference);
        Vec3 dirVec = (ejectorPos.subtract(startPos)).normalize();
        return dirVec.scale(velocity).add(0, -0.15, 0);
    }
}
