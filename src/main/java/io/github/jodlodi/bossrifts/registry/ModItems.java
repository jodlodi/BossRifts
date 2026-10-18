package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BossRifts.MOD_ID);

	public static final RegistryObject<RiftPearlItem> RIFT_PEARL = ITEMS.register("rift_pearl", () ->
			new RiftPearlItem(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));

	public static final RegistryObject<RiftAggregatorItem> RIFT_AGGREGATOR = ITEMS.register("rift_aggregator", () ->
			new RiftAggregatorItem(ModBlocks.RIFT_AGGREGATOR.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
