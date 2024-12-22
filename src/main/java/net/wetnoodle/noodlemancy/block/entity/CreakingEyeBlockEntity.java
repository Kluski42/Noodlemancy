package net.wetnoodle.noodlemancy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.wetnoodle.noodlemancy.registry.NMBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.wetnoodle.noodlemancy.block.CreakingEyeBlock.POWER;

public class CreakingEyeBlockEntity extends BlockEntity {
    public static final int RANGE = 32;
    public static final int RANGE_SQ = RANGE * RANGE;
    public static final double DETECTION_RANGE = 60.0 * Math.PI / 180.0;
    public static final double DOT_RANGE = Math.acos(DETECTION_RANGE);

    public CreakingEyeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NMBlockEntityTypes.CREAKING_EYE, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, CreakingEyeBlockEntity eyeBlockEntity) {
        calculatePower(level, blockPos, blockState);
    }

    private static List<Player> getPlayersInRange(Level level, BlockPos blockPos) {
        // the creaking mob remembers what players were nearby
        List<? extends Player> players = level.players();
        List<Player> output = new ArrayList<>();
        for (Player player : players) {
            double dist = player.getEyePosition().distanceToSqr(blockPos.getCenter());
            if (dist <= RANGE) {
                output.add(player);
            }
        }
        return output;
    }

    private static void updatePower(@Nullable Entity entity, Level level, BlockPos blockPos, BlockState blockState, int newPower) {
        if (blockState.getValue(POWER) != newPower) {
            level.setBlock(blockPos, blockState.setValue(POWER, newPower), Block.UPDATE_ALL);
        }
    }

    private static void calculatePower(Level level, BlockPos blockPos, BlockState blockState) {
        double closetDot = 0;
        Player mostAccuratePlayer = null;
        // the creaking mob remembers what players were nearby
        List<? extends Player> players = level.players();
        for (Player player : players) {
            double dist = player.getEyePosition().distanceToSqr(blockPos.getCenter());
            if (dist <= RANGE_SQ) {
                double dot = getAccuracy(player, level, blockPos, blockState, dist);
                if (dot > closetDot) {
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
        updatePower(mostAccuratePlayer, level, blockPos, blockState, power);
    }

    private static double getAccuracy(LivingEntity entity, Level level, BlockPos blockPos, BlockState blockState, double distance) {
        Vec3 viewVector = entity.getViewVector(1.0F).normalize();
        Vec3 relativeEntityPos = blockPos.getCenter().subtract(entity.getEyePosition()).normalize();
        double dot = viewVector.dot(relativeEntityPos);
        return dot;
//        double reqDot = 1 - 0.5 / distance;
//        if (dot > reqDot) {
//            return dot;
//        }
//        return 0;
    }
}
