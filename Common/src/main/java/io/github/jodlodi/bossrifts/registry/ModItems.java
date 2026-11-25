package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModItems {
	public static final MultiSupplier<RiftPearlItem> RIFT_PEARL = Services.REGISTRY.registerItem("rift_pearl", properties ->
			new RiftPearlItem(properties.stacksTo(1))
	);

	public static final MultiSupplier<RiftAggregatorItem> RIFT_AGGREGATOR = Services.REGISTRY.registerItem("rift_aggregator", properties ->
			new RiftAggregatorItem(ModBlocks.RIFT_AGGREGATOR.get(), properties)
	);

	public static void init() {

	}
}
