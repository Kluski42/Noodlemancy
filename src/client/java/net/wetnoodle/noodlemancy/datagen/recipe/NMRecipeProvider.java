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

import static net.minecraft.data.recipes.RecipeProvider.getHasName;

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
                getCreakingEyeRecipe(this).save(output);
                getSneezerRecipe(this).save(output);
            }
        };
    }

    private RecipeBuilder getCreakingEyeRecipe(RecipeProvider recipeProvider) {
        return recipeProvider.shaped(RecipeCategory.REDSTONE, NMBlocks.CREAKING_EYE)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('H', Blocks.CREAKING_HEART)
                .pattern("IRI")
                .pattern("IHI")
                .pattern("IRI")
                .unlockedBy(getHasName(Blocks.CREAKING_HEART), recipeProvider.has(Blocks.CREAKING_HEART));
    }

    private RecipeBuilder getSneezerRecipe(RecipeProvider recipeProvider) {
        return recipeProvider.shaped(RecipeCategory.REDSTONE, NMBlocks.SNEEZER)
                .define('I', Items.IRON_INGOT)
                .define('D', Blocks.DROPPER)
                .define('B', Items.BREEZE_ROD)
                .define('S', Blocks.SMOOTH_STONE)
                .pattern("IDI")
                .pattern("IBI")
                .pattern("SSS")
                .unlockedBy(getHasName(Items.BREEZE_ROD), recipeProvider.has(Items.BREEZE_ROD));
    }

    private void build(RecipeProvider recipeProvider, RecipeBuilder recipeBuilder, ItemLike itemLike2, RecipeOutput output) {
        recipeBuilder.unlockedBy(getHasName(itemLike2), recipeProvider.has(itemLike2)).save(output);
    }

    @Override
    @NotNull
    public String getName() {
        return "Noodlemancy Recipes";
    }
}
