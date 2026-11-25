package io.github.jodlodi.bossrifts.client;

import io.github.jodlodi.bossrifts.client.rift.RiftEntityRenderer;
import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import io.github.jodlodi.bossrifts.platform.FabricNetworkHelper;
import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ClientInit implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ParticleProviderRegistry.getInstance().register(ModParticles.RIFT_SMOKE.get(), SmokeParticle.Provider::new);

		EntityRenderers.register(ModEntities.BOSS_RIFT.get(), RiftEntityRenderer::new);

		BlockEntityRenderers.register(ModBlockEntities.RIFT_AGGREGATOR.get(), RiftAggregatorRenderer::new);

		ModelLayerRegistry.registerModelLayer(iRiftRenderer.BOSS_RIFT_MODEL, iRiftRenderer::createBodyLayer);
		ModelLayerRegistry.registerModelLayer(RiftAggregatorRenderer.AGGREGATOR_RIFT_MODEL, RiftAggregatorRenderer::createLayer);

		FabricNetworkHelper.ModPacketHandler.init();

		ModelLoadingPlugin.register(new ModModelLoadingPlugin());
	}
}
