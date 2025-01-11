package net.wetnoodle.noodlemancy.datagen.model;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.wetnoodle.noodlemancy.NMConstants;

import java.util.Optional;

public class NMModelTemplates {
    public static final ModelTemplate CREAKING_EYE_TEMPLATE = create("observeresque_emissive", TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.BACK, NMTextureSlots.FRONT_EMISSIVE);
    public static final ModelTemplate CREAKING_EYE_OFF_TEMPLATE = create("observeresque", TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.BACK);


    private static ModelTemplate create(TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), textureSlots);
    }

    private static ModelTemplate create(String string, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(NMConstants.id("block/" + string)), Optional.empty(), textureSlots);
    }
}
