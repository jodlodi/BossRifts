package io.github.jodlodi.bossrifts.mixin;

import com.mojang.serialization.MapCodec;
import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(SpecialModelRenderers.class)
public abstract class SpecialModelRenderersMixin {
	@Shadow
	@Final
	private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At(value = "TAIL"))
	private static void onBootstrap(CallbackInfo ci) {
		ID_MAPPER.put(RiftItemRenderer.Unbaked.ID, RiftItemRenderer.Unbaked.MAP_CODEC);
	}
}