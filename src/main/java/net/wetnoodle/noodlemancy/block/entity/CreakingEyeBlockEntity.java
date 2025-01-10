package net.wetnoodle.noodlemancy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.wetnoodle.noodlemancy.block.CreakingEyeBlock;
import net.wetnoodle.noodlemancy.registry.NMBlockEntities;

import java.util.ArrayList;
import java.util.List;

import static net.wetnoodle.noodlemancy.block.CreakingEyeBlock.FACING;

public class CreakingEyeBlockEntity extends BlockEntity {
    public static final int RANGE = 32;
    public static final int RANGE_SQ = RANGE * RANGE;
    public static final double DETECTION_RANGE = 60.0 * Math.PI / 180.0;

    public CreakingEyeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NMBlockEntities.CREAKING_EYE, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, CreakingEyeBlockEntity eyeBlockEntity) {
        calculatePower(level, blockPos, blockState);
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
                if ((dot > closetDot) && (isVisible(level, player, blockPos, blockState, distance))) {
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
        CreakingEyeBlock.updatePower(level, blockPos, blockState, power);
    }

    private static boolean isVisible(Level level, Player player, BlockPos blockPos, BlockState blockState, double distance) {
        Direction facing = blockState.getValue(FACING);
        Vec3 eyePos = player.getEyePosition();
        Vec3 facingVec = facing.getUnitVec3().multiply(0.495, 0.495, 0.495);
        Vec3 creakingPos = blockPos.getCenter().add(facingVec);
        if (couldSeeFace(eyePos, creakingPos, facing)) {
            List<Vec3> offsets = getOffsets(facing);
            for (Vec3 offset : offsets) {
                BlockHitResult hitResult = level.clip(new ClipContext(eyePos, creakingPos.add(offset), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
                if (hitResult.getBlockPos().equals(blockPos)
                        && Math.abs(hitResult.getLocation().subtract(creakingPos).get(facing.getAxis())) < 0.01
                ) return true;
            }
        }
        return false;
    }

    private static boolean couldSeeFace(Vec3 playerVec, Vec3 creakingVec, Direction direction) {
        Direction.Axis axis = direction.getAxis();
        boolean isFacingPos = direction.getAxisDirection().getStep() == 1;
        double playerAxisPos = playerVec.get(axis);
        double creakingAxisPos = creakingVec.get(axis);
        if (isFacingPos) {
            return playerAxisPos >= creakingAxisPos;
        } else {
            return playerAxisPos <= creakingAxisPos;
        }
    }

    private static ArrayList<Vec3> getOffsets(Direction direction) {
        final double DIST = 0.4;
        ArrayList<Vec3> output = new ArrayList<>();
        output.add(Vec3.ZERO);
        Direction.Axis axis = direction.getAxis();
        if (!axis.equals(Direction.Axis.X)) {
            output.add(new Vec3(DIST, 0, 0));
            output.add(new Vec3(-DIST, 0, 0));
        }
        if (!axis.equals(Direction.Axis.Y)) {
            output.add(new Vec3(0, DIST, 0));
            output.add(new Vec3(0, -DIST, 0));
        }
        if (!axis.equals(Direction.Axis.Z)) {
            output.add(new Vec3(0, 0, DIST));
            output.add(new Vec3(0, 0, -DIST));
        }
        return output;
    }
}

