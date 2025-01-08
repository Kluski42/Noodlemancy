package net.wetnoodle.noodlemancy.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.wetnoodle.noodlemancy.registry.NMBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NMRecipeProvider extends FabricRecipeProvider {
    public NMRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.Provider registries = this.registries;
                RecipeOutput output = this.output;

                this.shaped(RecipeCategory.REDSTONE, NMBlocks.CREAKING_EYE)
                        .define('I', Items.IRON_INGOT)
                        .define('R', Items.REDSTONE)
                        .define('H', Blocks.CREAKING_HEART)
                        .pattern("IRI")
                        .pattern("IHI")
                        .pattern("IRI")
                        .unlockedBy(getHasName(Blocks.CREAKING_HEART), has(Blocks.CREAKING_HEART))
                        .save(output);
            }
        };
    }

    private void build(RecipeProvider recipeProvider, RecipeBuilder recipeBuilder, ItemLike itemLike2, RecipeOutput output) {
        recipeBuilder.unlockedBy(RecipeProvider.getHasName(itemLike2), recipeProvider.has(itemLike2)).save(output);
    }

    @Override
    @NotNull
    public String getName() {
        return "Noodlemancy Recipes";
    }
}
