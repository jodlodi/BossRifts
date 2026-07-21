package io.github.jodlodi.bossrifts.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.client.RiftRenderer;
import io.github.jodlodi.bossrifts.entities.RiftPearlEntity;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupListener {
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BOSS_RIFT.get(), RiftRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.RIFT_PEARL.get(), manager -> new SpriteRenderer<>(manager, event.getMinecraftSupplier().get().getItemRenderer(), 1.0F, true));
	}
}