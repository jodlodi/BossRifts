package io.github.jodlodi.bossrifts.mixin.model;

import com.mojang.serialization.MapCodec;
import io.github.jodlodi.bossrifts.client.rift.AnimatedItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModels.class)
public abstract class ItemModelsMixin {
	@Shadow
	@Final
	private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At(value = "TAIL"))
	private static void onBootstrap(CallbackInfo ci) {
		ID_MAPPER.put(AnimatedItemModel.ID, AnimatedItemModel.MAP_CODEC);
	}
}
