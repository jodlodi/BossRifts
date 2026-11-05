package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.network.ModPacketHandler;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EXPCostListener {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayer player) {
			Services.NETWORK.sendToClient(player, new SendRespawnDataPacket(
					Optional.ofNullable(player.getRespawnPosition()),
					Optional.of(player.getRespawnDimension())
			));
		}
	}

	@SubscribeEvent
	public static void onPlayerSetSpawnEvent(PlayerSetSpawnEvent event) {
		if (event.getPlayer() instanceof ServerPlayer player) {
			Services.NETWORK.sendToClient(player, new SendRespawnDataPacket(
					Optional.ofNullable(event.getNewSpawn()),
					Optional.of(event.getSpawnWorld())
			));
		}
	}
}