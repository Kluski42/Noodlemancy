package net.wetnoodle.noodlemancy.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.wetnoodle.noodlemancy.NMConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.item.CreativeModeTabs.*;

public class NMInventorySorting {
    public static void init() {
        NMConstants.log("Adding items for Noodlemancy to creative inventory", NMConstants.UNSTABLE_LOGGING);
        initRedstoneBlocks();
    }

    private static void initRedstoneBlocks() {
        addBefore(Blocks.SCULK_SENSOR, NMBlocks.CREAKING_EYE, REDSTONE_BLOCKS);
        addAfter(Blocks.DROPPER, NMBlocks.PRESSURIZED_DROPPER, REDSTONE_BLOCKS);
    }

    // Copied from FrozenLib

    @SafeVarargs
    private static void add(ItemLike item, @NotNull ResourceKey<CreativeModeTab>... tabs) {
        if (item != null) {
            ResourceKey[] var2 = tabs;
            int var3 = tabs.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                ResourceKey<CreativeModeTab> tab = var2[var4];
                ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
                    ItemStack stack = new ItemStack(item);
                    stack.setCount(1);
                    entries.accept(stack);
                });
            }
        }
    }

    @SafeVarargs
    private static void addBefore(ItemLike comparedItem, ItemLike item, ResourceKey<CreativeModeTab>... tabs) {
        addBefore(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
    }

    @SafeVarargs
    private static void addBefore(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, @NotNull ResourceKey<CreativeModeTab>... tabs) {
        if (comparedItem != null && item != null) {
            ResourceKey[] var4 = tabs;
            int var5 = tabs.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                ResourceKey<CreativeModeTab> tab = var4[var6];
                ItemStack stack = new ItemStack(item);
                stack.setCount(1);
                List<ItemStack> list = List.of(stack);
                ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
                    entries.addBefore(comparedItem, list, tabVisibility);
                });
            }
        }
    }


    @SafeVarargs
    private static void addAfter(ItemLike comparedItem, ItemLike item, ResourceKey<CreativeModeTab>... tabs) {
        addAfter(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
    }

    @SafeVarargs
    private static void addAfter(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, @NotNull ResourceKey<CreativeModeTab>... tabs) {
        if (comparedItem != null && item != null) {
            item.asItem();
            ResourceKey[] var4 = tabs;
            int var5 = tabs.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                ResourceKey<CreativeModeTab> tab = var4[var6];
                ItemStack stack = new ItemStack(item);
                stack.setCount(1);
                List<ItemStack> list = List.of(stack);
                ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
                    entries.addAfter(comparedItem, list, tabVisibility);
                });
            }
        }
    }

}

