package net.wetnoodle.noodlemancy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.wetnoodle.noodlemancy.registry.NMBlocks;

public class NoodlemancyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(NMBlocks.CREAKING_EYE, RenderType.cutout());
	}
}