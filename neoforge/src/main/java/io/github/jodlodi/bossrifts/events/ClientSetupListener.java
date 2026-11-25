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
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientSetupListener {
	public static final StandaloneModelKey<QuadCollection> KEY = new StandaloneModelKey<>(iRiftRenderer.PEARL_ITEM_MODEL::toDebugFileName);

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		EntityRenderers.register(ModEntities.BOSS_RIFT.get(), RiftEntityRenderer::new);
		BlockEntityRenderers.register(ModBlockEntities.RIFT_AGGREGATOR.get(), RiftAggregatorRenderer::new);
	}

	@SubscribeEvent
	public static void registerStandalone(ModelEvent.RegisterStandalone event) {
		event.register(KEY, SimpleUnbakedStandaloneModel.quadCollection(iRiftRenderer.PEARL_ITEM_MODEL));
	}

	@SubscribeEvent
	public static void registerFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticles.RIFT_SMOKE.get(), SmokeParticle.Provider::new);
	}

	@SubscribeEvent
	public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(iRiftRenderer.BOSS_RIFT_MODEL, iRiftRenderer::createBodyLayer);
		event.registerLayerDefinition(RiftAggregatorRenderer.AGGREGATOR_RIFT_MODEL, RiftAggregatorRenderer::createLayer);
	}
}