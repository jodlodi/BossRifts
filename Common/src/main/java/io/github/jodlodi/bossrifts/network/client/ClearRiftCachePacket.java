package io.github.jodlodi.bossrifts.network.client;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ClearRiftCachePacket extends ClientBoundPacket {
	public static final ResourceLocation ID = Constants.prefix("clear_cache");

	public ClearRiftCachePacket() {

	}

	public static ClearRiftCachePacket decode(FriendlyByteBuf buffer) {
		return new ClearRiftCachePacket();
	}

	@Override
	public FriendlyByteBuf encode(FriendlyByteBuf buf) {
		return buf;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public void handle() {
		Handler.onMessage(this);
	}

	public static class Handler {
		public static void onMessage(ClearRiftCachePacket message) {
			RiftItemRenderer.STACK_MAP.clear();
		}
	}
}
