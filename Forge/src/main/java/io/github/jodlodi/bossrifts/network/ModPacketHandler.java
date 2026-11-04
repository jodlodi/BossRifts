package io.github.jodlodi.bossrifts.network;

import io.github.jodlodi.bossrifts.Constants;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModPacketHandler {
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
		CHANNEL.messageBuilder(SendRespawnDataPacket.class, id++).encoder(SendRespawnDataPacket::encode).decoder(SendRespawnDataPacket::decode).consumer(SendRespawnDataPacket.Handler::onMessage).add();
	}
}
