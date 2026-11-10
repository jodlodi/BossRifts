package io.github.jodlodi.bossrifts.client;

import io.github.jodlodi.bossrifts.client.rift.RiftEntityRenderer;
import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import io.github.jodlodi.bossrifts.platform.FabricNetworkHelper;
import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.renderer.RenderType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ClientInit implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(ModParticles.RIFT_SMOKE.get(), SmokeParticle.Provider::new);

		EntityRendererRegistry.register(ModEntities.BOSS_RIFT.get(), RiftEntityRenderer::new);

		BlockEntityRendererRegistry.register(ModBlockEntities.RIFT_AGGREGATOR.get(), RiftAggregatorRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.RIFT_AGGREGATOR.get());

		EntityModelLayerRegistry.registerModelLayer(iRiftRenderer.BOSS_RIFT_MODEL, iRiftRenderer::createBodyLayer);
		EntityModelLayerRegistry.registerModelLayer(RiftAggregatorRenderer.AGGREGATOR_RIFT_MODEL, RiftAggregatorRenderer::createLayer);

		FabricNetworkHelper.ModPacketHandler.init();
	}
}
