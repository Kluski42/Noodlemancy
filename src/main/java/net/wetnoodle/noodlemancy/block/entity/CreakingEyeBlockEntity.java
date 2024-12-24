package net.wetnoodle.noodlemancy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.wetnoodle.noodlemancy.registry.NMBlockEntityTypes;

import java.util.List;

import static net.wetnoodle.noodlemancy.block.CreakingEyeBlock.EYES;
import static net.wetnoodle.noodlemancy.block.CreakingEyeBlock.POWER;

public class CreakingEyeBlockEntity extends BlockEntity {
    public static final int RANGE = 32;
    public static final int RANGE_SQ = RANGE * RANGE;
    public static final double DETECTION_RANGE = 60.0 * Math.PI / 180.0;

    public CreakingEyeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NMBlockEntityTypes.CREAKING_EYE, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, CreakingEyeBlockEntity eyeBlockEntity) {
        calculatePower(level, blockPos, blockState);
    }

    private static void updatePower(Level level, BlockPos blockPos, BlockState blockState, int newPower) {
        if (blockState.getValue(POWER) != newPower) {
            level.setBlock(blockPos, blockState.setValue(POWER, newPower).setValue(EYES, (int) Math.ceil(newPower / 5.0)), Block.UPDATE_ALL);
        }
    }

    private static void calculatePower(Level level, BlockPos blockPos, BlockState blockState) {
        double closetDot = 0;
        Player mostAccuratePlayer = null;
        // the creaking mob remembers what players were nearby
        List<? extends Player> players = level.players();
        for (Player player : players) {
            if (!LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(player) || player.isSpectator()) {
                continue;
            }
            double distance = player.getEyePosition().distanceToSqr(blockPos.getCenter());
            if (distance <= RANGE_SQ) {
                Vec3 viewVector = player.getViewVector(1.0F).normalize();
                Vec3 relativeEntityPos = blockPos.getCenter().subtract(player.getEyePosition()).normalize();
                double dot = viewVector.dot(relativeEntityPos);
                if ((dot > closetDot) && (isVisible(level, player, blockPos, distance))) {
                    closetDot = dot;
                    mostAccuratePlayer = player;
                }
            }
        }
        // Note that 0 <= angle <= 180.
        double angle = Math.acos(closetDot);
        int power = 0;
        if (angle <= DETECTION_RANGE) {
            power = (int) Math.ceil((1 - (angle / DETECTION_RANGE)) * 15);
        }
        updatePower(level, blockPos, blockState, power);
    }

    private static boolean isVisible(Level level, Player player, BlockPos blockPos, double distance) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 creakingPos = blockPos.getCenter();
        Vec3[] offsets = new Vec3[]{Vec3.ZERO, Direction.UP.getUnitVec3().multiply(1, 0.5, 1), Direction.DOWN.getUnitVec3().multiply(1, 0.5, 1)};
        for (Vec3 offset : offsets) {
            BlockHitResult hitResult = level.clip(new ClipContext(eyePos, creakingPos.add(offset), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
            if (hitResult.getBlockPos().equals(blockPos)) return true;
        }
        return false;
    }
}

