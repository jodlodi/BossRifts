package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.client.RiftAggregatorRenderer;
import io.github.jodlodi.bossrifts.client.rift.RiftEntityRenderer;
import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupListener {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		EntityRenderers.register(ModEntities.BOSS_RIFT.get(), RiftEntityRenderer::new);
		BlockEntityRenderers.register(ModBlockEntities.RIFT_AGGREGATOR.get(), RiftAggregatorRenderer::new);
	}

	@SubscribeEvent
	public static void onModelRegister(ModelEvent.RegisterAdditional event) {
		event.register(iRiftRenderer.FRAME_MODEL);
		event.register(iRiftRenderer.PEARL_MODEL);
	}

	@SubscribeEvent
	public static void registerFactories(RegisterParticleProvidersEvent event) {
		event.register(ModParticles.RIFT_SMOKE.get(), SmokeParticle.Provider::new);
	}

	@SubscribeEvent
	public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(iRiftRenderer.BOSS_RIFT_MODEL, iRiftRenderer::createBodyLayer);
		event.registerLayerDefinition(RiftAggregatorRenderer.AGGREGATOR_RIFT_MODEL, RiftAggregatorRenderer::createLayer);
	}
}