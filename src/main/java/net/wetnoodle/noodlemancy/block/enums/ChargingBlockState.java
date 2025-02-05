package net.wetnoodle.noodlemancy.block.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ChargingBlockState implements StringRepresentable {
    UNPOWERED("unpowered"),
    CHARGING("charging"),
    HOLDING("holding"),
    TRIGGERED("triggered");


    private final String name;

    ChargingBlockState(final String string) {
        this.name = string;
    }

    /**
     * {@return the unique string representation of the enum, used for serialization}
     */
    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
