package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BossRifts.MOD_ID);

	public static final RegistryObject<RiftPearlItem> RIFT_PEARL = ITEMS.register("rift_pearl", () ->
			new RiftPearlItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

	public static final RegistryObject<RiftAggregatorItem> RIFT_AGGREGATOR = ITEMS.register("rift_aggregator", () ->
			new RiftAggregatorItem(ModBlocks.RIFT_AGGREGATOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
