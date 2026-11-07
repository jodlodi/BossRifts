package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
	@Shadow private Collection<ItemStack> displayItems;
	@Shadow private Set<ItemStack> displayItemsSearchTab;

	@Inject(method = "buildContents", at = @At("TAIL"))
	private void bossrifts$addItems(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
		if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey((CreativeModeTab) (Object) this).orElse(null))) {
			ItemStack pearl = new ItemStack(ModItems.RIFT_PEARL.get());
			ItemStack aggregator = new ItemStack(ModItems.RIFT_AGGREGATOR.get());

			this.displayItems.add(pearl);
			this.displayItems.add(aggregator);
			this.displayItemsSearchTab.add(pearl);
			this.displayItemsSearchTab.add(aggregator);
		}
	}
}
