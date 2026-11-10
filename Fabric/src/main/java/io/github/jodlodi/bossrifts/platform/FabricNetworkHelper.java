package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.network.IPacket;
import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.ClientBoundConfigPacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricNetworkHelper implements INetworkHelper {

	@Override
	public void sendToClient(ServerPlayer player, IPacket packet) {
		ServerPlayNetworking.send(player, packet);
	}

	public static void init() {
		PayloadTypeRegistry.playS2C().register(SendRespawnDataPacket.TYPE, SendRespawnDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ClearRiftCachePacket.TYPE, ClearRiftCachePacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ClientBoundConfigPacket.TYPE, ClientBoundConfigPacket.STREAM_CODEC);
	}

	public static class ModPacketHandler {
		public static void init() {
			ClientPlayNetworking.registerGlobalReceiver(SendRespawnDataPacket.TYPE, (packet, context) -> context.client().execute(packet::handle));
			ClientPlayNetworking.registerGlobalReceiver(ClearRiftCachePacket.TYPE, (packet, context) -> context.client().execute(packet::handle));
			ClientPlayNetworking.registerGlobalReceiver(ClientBoundConfigPacket.TYPE, (packet, context) -> context.client().execute(packet::handle));
		}
	}
}