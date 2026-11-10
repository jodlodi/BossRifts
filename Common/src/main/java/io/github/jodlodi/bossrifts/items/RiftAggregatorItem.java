package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.List;

@NullMarked
public class RiftAggregatorItem extends BlockItem {
	public static final String TOOLTIP = "tooltip.boss_rifts.aggregator";

	public RiftAggregatorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (Services.CONFIG.canSetSpawn()) list.add(Component.translatable(TOOLTIP));
	}
}
