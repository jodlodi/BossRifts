package io.github.jodlodi.bossrifts.registry;

import com.mojang.serialization.Codec;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.LodestoneTracker;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModDataComponents {
	public static final MultiSupplier<DataComponentType<LodestoneTracker>> AGGREGATOR_TRACKER = Services.REGISTRY.registerDataComponentType("aggregator_tracker", (builder) ->
			builder.persistent(LodestoneTracker.CODEC).networkSynchronized(LodestoneTracker.STREAM_CODEC).cacheEncoding());

	public static final MultiSupplier<DataComponentType<Long>> POWERED_MARKER = Services.REGISTRY.registerDataComponentType("powered_marker", (builder) ->
			builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

	public static void init() {

	}
}
