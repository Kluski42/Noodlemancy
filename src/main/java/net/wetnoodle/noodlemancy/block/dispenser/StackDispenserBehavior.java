package net.wetnoodle.noodlemancy.block.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class StackDispenserBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(blockSource);
        ItemStack itemStack = stack.copyAndClear();
        noodlemancy$spawnItem(blockSource.level(), itemStack, 6, direction, position);
        return stack;
    }

    public static void noodlemancy$spawnItem(Level level, ItemStack stack, int i, Direction direction, Position position) {
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

}
