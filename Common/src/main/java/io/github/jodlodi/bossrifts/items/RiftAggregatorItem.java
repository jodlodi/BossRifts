package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class RiftAggregatorItem extends BlockItem {
	public static final String TOOLTIP = "tooltip.boss_rifts.aggregator";

	public RiftAggregatorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		if (Services.CONFIG.canSetSpawn()) builder.accept(Component.translatable(TOOLTIP));
	}
}
