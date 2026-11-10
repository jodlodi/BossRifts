package io.github.jodlodi.bossrifts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IPacket extends CustomPacketPayload {

	void handle();

	FriendlyByteBuf encode(FriendlyByteBuf buf);
}
