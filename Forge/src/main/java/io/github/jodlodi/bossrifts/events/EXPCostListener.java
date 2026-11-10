package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EXPCostListener {

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			Services.NETWORK.sendToClient(player, new SendRespawnDataPacket(player.getRespawnPosition(), player.getRespawnDimension()));
			Services.NETWORK.sendToClient(player, new ClearRiftCachePacket());
		}
	}

	@SubscribeEvent
	public static void onPlayerSetSpawnEvent(PlayerSetSpawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			Services.NETWORK.sendToClient(player, new SendRespawnDataPacket(event.getNewSpawn(), event.getSpawnLevel()));
		}
	}
}