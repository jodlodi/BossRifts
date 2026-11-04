package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModItems {
	public static final MultiSupplier<RiftPearlItem> RIFT_PEARL = Services.REGISTRY.registerItem("rift_pearl", () ->
			new RiftPearlItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

	public static final MultiSupplier<RiftAggregatorItem> RIFT_AGGREGATOR = Services.REGISTRY.registerItem("rift_aggregator", () ->
			new RiftAggregatorItem(ModBlocks.RIFT_AGGREGATOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

	public static void init() {

	}
}
