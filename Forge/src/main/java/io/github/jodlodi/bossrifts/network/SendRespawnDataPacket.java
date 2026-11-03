package io.github.jodlodi.bossrifts.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.function.Supplier;

@NullMarked
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "Convert2Lambda"})
public class SendRespawnDataPacket {
	public static final Codec<SendRespawnDataPacket> CODEC = RecordCodecBuilder.create((recordCodecBuilder) -> recordCodecBuilder.group(
			BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(message -> message.respawnPosition),
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(message -> message.respawnDimension)
	).apply(recordCodecBuilder, SendRespawnDataPacket::new));

	private final Optional<BlockPos> respawnPosition;
	private final Optional<ResourceKey<Level>> respawnDimension;

	public SendRespawnDataPacket(Optional<BlockPos> respawnPosition, Optional<ResourceKey<Level>> respawnDimension) {
		this.respawnPosition = respawnPosition;
		this.respawnDimension = respawnDimension;
	}

	public static SendRespawnDataPacket decode(FriendlyByteBuf buffer) {
		return buffer.readWithCodec(CODEC);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeWithCodec(CODEC, this);
	}

	public static class Handler {
		public static boolean onMessage(SendRespawnDataPacket message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(new Runnable() {
				@Override
				public void run() {
					RiftPearlItem.respawnPosition = message.respawnPosition.orElse(null);
					RiftPearlItem.respawnDimension = message.respawnDimension.orElse(null);
				}
			});
			return true;
		}
	}
}
