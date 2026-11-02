package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.RiftConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftAggregatorItem extends BlockItem {
	public static final String TOOLTIP = "tooltip.boss_rifts.aggregator";

	public RiftAggregatorItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> list, ITooltipFlag flag) {
		if (RiftConfig.canSetSpawn()) list.add(new TranslationTextComponent(TOOLTIP));
	}
}
