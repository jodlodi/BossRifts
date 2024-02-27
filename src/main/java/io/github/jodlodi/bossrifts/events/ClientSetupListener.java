package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.registry.Reg;
import io.github.jodlodi.bossrifts.rift.RiftRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = BossRifts.MOD_ID)
public class ClientSetupListener {
    public static final ModelLayerLocation BOSS_RIFT_MODEL =  new ModelLayerLocation(Reg.riftResource("boss_rift"), "main");

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BOSS_RIFT_MODEL, RiftRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void clientSetup(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Reg.BOSS_RIFT.get(), RiftRenderer::new);
    }
}