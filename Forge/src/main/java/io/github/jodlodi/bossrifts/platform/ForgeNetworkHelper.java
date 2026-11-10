package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.network.IPacket;
import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
public class ForgeNetworkHelper implements INetworkHelper {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			Constants.prefix("channel"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	@SuppressWarnings("UnusedAssignment")
	public static void init() {
		int id = 0;
		CHANNEL.messageBuilder(SendRespawnDataPacket.class, id++).encoder(SendRespawnDataPacket::encode).decoder(SendRespawnDataPacket::decode).consumer((SimpleChannel.MessageBuilder.ToBooleanBiFunction<SendRespawnDataPacket, Supplier<NetworkEvent.Context>>) ForgeNetworkHelper::onMessage).add();
		CHANNEL.messageBuilder(ClearRiftCachePacket.class, id++).encoder(ClearRiftCachePacket::encode).decoder(ClearRiftCachePacket::decode).consumer((SimpleChannel.MessageBuilder.ToBooleanBiFunction<ClearRiftCachePacket, Supplier<NetworkEvent.Context>>) ForgeNetworkHelper::onMessage).add();
	}

	@SuppressWarnings({"Anonymous2MethodRef", "Convert2Lambda"})
	public static <T extends IPacket> boolean onMessage(T t, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(new Runnable() {
			@Override
			public void run() {
				t.handle();
			}
		});
		return true;
	}

	@Override
	public void sendToClient(ServerPlayer player, IPacket packet) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}
}