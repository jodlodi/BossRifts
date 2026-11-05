package io.github.jodlodi.bossrifts.network.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
public class SendRespawnDataPacket extends ClientBoundPacket {
	public static final Codec<SendRespawnDataPacket> CODEC = RecordCodecBuilder.create((recordCodecBuilder) -> recordCodecBuilder.group(
			BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(message -> message.respawnPosition),
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(message -> message.respawnDimension)
	).apply(recordCodecBuilder, SendRespawnDataPacket::new));
	public static final ResourceLocation ID = Constants.prefix("send_respawn_data");

	private final Optional<BlockPos> respawnPosition;
	private final Optional<ResourceKey<Level>> respawnDimension;

	public SendRespawnDataPacket(Optional<BlockPos> respawnPosition, Optional<ResourceKey<Level>> respawnDimension) {
		this.respawnPosition = respawnPosition;
		this.respawnDimension = respawnDimension;
	}

	public static SendRespawnDataPacket decode(FriendlyByteBuf buffer) {
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
		public static void onMessage(SendRespawnDataPacket message) {
			RiftPearlItem.respawnPosition = message.respawnPosition.orElse(null);
			RiftPearlItem.respawnDimension = message.respawnDimension.orElse(null);
		}
	}
}
