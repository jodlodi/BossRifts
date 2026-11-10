package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.network.IPacket;
import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeNetworkHelper implements INetworkHelper {
	private static final String PROTOCOL_VERSION = "1";

	@SubscribeEvent
	public static void onRegEvent(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).versioned(PROTOCOL_VERSION).optional();

		registrar.playToClient(SendRespawnDataPacket.TYPE, SendRespawnDataPacket.STREAM_CODEC, NeoForgeNetworkHelper::onMessage);
		registrar.playToClient(ClearRiftCachePacket.TYPE, ClearRiftCachePacket.STREAM_CODEC, NeoForgeNetworkHelper::onMessage);
	}

	@SuppressWarnings("NullableProblems")
	public static <T extends IPacket> void onMessage(@NonNull T message, IPayloadContext ctx) {
		ctx.enqueueWork(message::handle);
	}

	@Override
	public void sendToClient(ServerPlayer player, IPacket packet) {
		PacketDistributor.sendToPlayer(player, packet);
	}
}