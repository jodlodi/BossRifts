package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Shadow
	protected abstract void loadTopLevel(ModelResourceLocation modelResourceLocation);

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadTopLevel(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 2))
	private void onInit(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profilerFiller, int i, CallbackInfo ci) {
		this.loadTopLevel(new ModelResourceLocation(iRiftRenderer.ENTITY_MODEL, "inventory"));
		this.loadTopLevel(new ModelResourceLocation(iRiftRenderer.EYE_MODEL, "inventory"));
	}
}
