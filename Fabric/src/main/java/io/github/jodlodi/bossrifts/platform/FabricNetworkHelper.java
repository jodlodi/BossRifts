package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.network.IPacket;
import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.ClientBoundConfigPacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricNetworkHelper implements INetworkHelper {

	@Override
	public void sendToClient(ServerPlayer player, IPacket packet) {
		ServerPlayNetworking.send(player, packet.getID(), packet.encode(PacketByteBufs.create()));
	}

	public static class ModPacketHandler {
		public static void init() {
			ClientPlayNetworking.registerGlobalReceiver(SendRespawnDataPacket.ID, (client, handler, buf, responseSender) -> SendRespawnDataPacket.decode(buf).handle());
			ClientPlayNetworking.registerGlobalReceiver(ClearRiftCachePacket.ID, (client, handler, buf, responseSender) -> ClearRiftCachePacket.decode(buf).handle());
			ClientPlayNetworking.registerGlobalReceiver(ClientBoundConfigPacket.ID, (client, handler, buf, responseSender) -> ClientBoundConfigPacket.decode(buf).handle());
		}
	}
}