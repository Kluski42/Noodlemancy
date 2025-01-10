package net.wetnoodle.noodlemancy.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.wetnoodle.noodlemancy.NMConstants;

public class NMBlockTags {
    public static final TagKey<Block> AIR_CAN_PASS_THROUGH = register("wind_can_pass_through");
    public static final TagKey<Block> AIR_CANNOT_PASS_THROUGH = register("wind_cannot_pass_through");

    private static TagKey<Block> register(String path) {
        return TagKey.create(Registries.BLOCK, NMConstants.id(path));
    }
}
