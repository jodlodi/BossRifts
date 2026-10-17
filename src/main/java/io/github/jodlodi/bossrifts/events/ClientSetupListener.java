package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.client.RiftAggregatorRenderer;
import io.github.jodlodi.bossrifts.client.rift.RiftEntityRenderer;
import io.github.jodlodi.bossrifts.items.RiftPearl;
import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupListener {
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BOSS_RIFT.get(), RiftEntityRenderer::new);

		ClientRegistry.bindTileEntityRenderer(ModBlockEntities.RIFT_AGGREGATOR.get(), RiftAggregatorRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.RIFT_AGGREGATOR.get(), RenderType.cutout());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.addSpecialModel(RiftPearl.ENTITY_MODEL);
		ModelLoader.addSpecialModel(RiftPearl.EYE_MODEL);
	}
}