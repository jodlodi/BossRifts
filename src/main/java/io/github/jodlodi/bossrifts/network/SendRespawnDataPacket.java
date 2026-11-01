package io.github.jodlodi.bossrifts.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "Convert2Lambda"})
public class SendRespawnDataPacket {
	public static final Codec<SendRespawnDataPacket> CODEC = RecordCodecBuilder.create((recordCodecBuilder) -> recordCodecBuilder.group(
			BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(message -> message.respawnPosition),
			World.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(message -> message.respawnDimension)
	).apply(recordCodecBuilder, SendRespawnDataPacket::new));

	private final Optional<BlockPos> respawnPosition;
	private final Optional<RegistryKey<World>> respawnDimension;

	public SendRespawnDataPacket(Optional<BlockPos> respawnPosition, Optional<RegistryKey<World>> respawnDimension) {
		this.respawnPosition = respawnPosition;
		this.respawnDimension = respawnDimension;
	}

	public static SendRespawnDataPacket decode(PacketBuffer buffer) {
		try {
			return buffer.readWithCodec(CODEC);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void encode(PacketBuffer buf) {
		try {
			buf.writeWithCodec(CODEC, this);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
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
