package io.github.jodlodi.bossrifts.network.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.FabricConfigHelper;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ClientBoundConfigPacket extends ClientBoundPacket {
	public static final Codec<ClientBoundConfigPacket> CODEC = RecordCodecBuilder.create((recordCodecBuilder) -> recordCodecBuilder.group(
			CompoundTag.CODEC.fieldOf("tag").forGetter(message -> message.tag)
	).apply(recordCodecBuilder, ClientBoundConfigPacket::new));
	public static final ResourceLocation ID = Constants.prefix("config_packet");

	private final CompoundTag tag;

	public ClientBoundConfigPacket(CompoundTag tag) {
		this.tag = tag;
	}

	public static ClientBoundConfigPacket decode(FriendlyByteBuf buffer) {
		return buffer.readWithCodec(CODEC);
	}

	@Override
	public FriendlyByteBuf encode(FriendlyByteBuf buf) {
		buf.writeWithCodec(CODEC, this);
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
		public static void onMessage(ClientBoundConfigPacket message) {
			FabricConfigHelper.getHelper().loadFromTag(message.tag);
		}
	}
}
