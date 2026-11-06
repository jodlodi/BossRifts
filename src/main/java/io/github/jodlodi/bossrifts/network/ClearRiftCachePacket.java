package io.github.jodlodi.bossrifts.network;

import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"Convert2Lambda"})
public class ClearRiftCachePacket {

	public ClearRiftCachePacket() {

	}

	public static ClearRiftCachePacket decode(PacketBuffer buffer) {
		return new ClearRiftCachePacket();
	}

	public void encode(PacketBuffer buf) {

	}

	public static class Handler {
		public static boolean onMessage(ClearRiftCachePacket message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(new Runnable() {
				@Override
				public void run() {
					RiftItemRenderer.STACK_MAP.clear();
				}
			});
			return true;
		}
	}
}
