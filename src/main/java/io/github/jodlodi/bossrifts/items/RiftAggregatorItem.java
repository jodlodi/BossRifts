package io.github.jodlodi.bossrifts.items;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftAggregatorItem extends BlockItem {
	public RiftAggregatorItem(Block block, Properties properties) {
		super(block, properties);
	}
}
