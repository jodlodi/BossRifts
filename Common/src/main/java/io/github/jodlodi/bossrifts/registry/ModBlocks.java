package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlock;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModBlocks {
	public static final MultiSupplier<RiftAggregatorBlock> RIFT_AGGREGATOR = Services.REGISTRY.registerBlock("rift_aggregator", () ->
			new RiftAggregatorBlock(Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((state) -> 12))
	);

	public static void init() {

	}
}
