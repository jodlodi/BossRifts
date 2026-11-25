package io.github.jodlodi.bossrifts.mixin.model;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.client.V2ModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectItemModelProperties.class)
public abstract class SelectItemModelPropertiesMixin {
	@Shadow
	@Final
	private static ExtraCodecs.LateBoundIdMapper<Identifier, SelectItemModelProperty.Type<?, ?>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At(value = "TAIL"))
	private static void onBootstrap(CallbackInfo ci) {
		ID_MAPPER.put(Constants.prefix("v2"), V2ModelProperty.TYPE);
	}
}
