package io.github.jodlodi.bossrifts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IPacket {
	ResourceLocation getID();

	void handle();

	FriendlyByteBuf encode(FriendlyByteBuf buf);
}
