package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.network.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.ModPacketHandler;
import io.github.jodlodi.bossrifts.network.SendRespawnDataPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class EXPCostListener {

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			ModPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SendRespawnDataPacket(
					Optional.ofNullable(player.getRespawnPosition()),
					Optional.of(player.getRespawnDimension())
			));
			ModPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClearRiftCachePacket());
		}
	}

	@SubscribeEvent
	public static void onPlayerSetSpawnEvent(PlayerSetSpawnEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			ModPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SendRespawnDataPacket(
					Optional.ofNullable(event.getNewSpawn()),
					Optional.of(event.getSpawnWorld())
			));
		}
	}
}