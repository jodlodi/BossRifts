package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
	public static final MultiSupplier<BlockEntityType<RiftAggregatorBlockEntity>> RIFT_AGGREGATOR = Services.REGISTRY.registerBlockEntity("rift_aggregator", () ->
			BlockEntityType.Builder.of(RiftAggregatorBlockEntity::new, ModBlocks.RIFT_AGGREGATOR.get()).build(null)
	);

	public static void init() {

	}
}
