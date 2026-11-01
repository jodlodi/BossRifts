package io.github.jodlodi.bossrifts.network;

import io.github.jodlodi.bossrifts.BossRifts;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModPacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			BossRifts.prefix("channel"),
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
