package net.wetnoodle.noodlemancy.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.wetnoodle.noodlemancy.datagen.loot.NMBlockLootProvider;
import net.wetnoodle.noodlemancy.datagen.recipe.NMRecipeProvider;
import net.wetnoodle.noodlemancy.datagen.tag.NMBlockTagProvider;
import net.wetnoodle.noodlemancy.datagen.tag.NMItemTagProvider;

public class NoodlemancyDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        final FabricDataGenerator.Pack pack = dataGenerator.createPack();

        // ASSETS

//        pack.addProvider(NMModelProvider::new);

        // DATA

        pack.addProvider(NMBlockLootProvider::new);
        pack.addProvider(NMRecipeProvider::new);
        pack.addProvider(NMItemTagProvider::new);
        pack.addProvider(NMBlockTagProvider::new);
    }
}
