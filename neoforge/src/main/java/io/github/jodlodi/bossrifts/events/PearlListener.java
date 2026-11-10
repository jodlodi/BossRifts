package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@EventBusSubscriber(modid = Constants.MOD_ID)
public class PearlListener {

	@SubscribeEvent
	public static void onPlayerPearl(EntityTeleportEvent.EnderPearl event) {
		ServerPlayer serverPlayer = event.getPlayer();
		ThrownEnderpearl pearl = event.getPearlEntity();
		MinecraftServer server = pearl.getServer();
		List<BossRiftEntity> nearbyRifts = pearl.level().getEntitiesOfClass(BossRiftEntity.class, pearl.getBoundingBox().inflate(1.6D), Entity::isAlive);
		if (!nearbyRifts.isEmpty()) {
			BossRiftEntity rift = nearbyRifts.getFirst();
			if (server != null) {
				event.setCanceled(true);
				serverPlayer.fallDistance = 0.0F;
				serverPlayer.hurt(serverPlayer.damageSources().fall(), event.getAttackDamage());
				server.tell(new TickTask(server.getTickCount(), () -> rift.sendToSpawn(rift, server, serverPlayer, serverPlayer)));
				server.tell(new TickTask(server.getTickCount(), () -> rift.validateSpawn(serverPlayer, false)));
			}
		}
	}
}