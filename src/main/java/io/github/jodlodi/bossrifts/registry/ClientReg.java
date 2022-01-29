package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.rift.RiftRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class ClientReg {
    public static final ModelLayerLocation BOSS_RIFT_MODEL =  new ModelLayerLocation(Reg.riftResource("boss_rift"), "main");

    @ParametersAreNonnullByDefault
    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BOSS_RIFT_MODEL, RiftRenderer::createBodyLayer);
    }
}
