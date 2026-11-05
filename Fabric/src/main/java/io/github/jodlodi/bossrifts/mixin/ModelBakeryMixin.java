package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Shadow
	public abstract UnbakedModel getModel(ResourceLocation resourceLocation);

	@Shadow
	@Final
	private Map<ResourceLocation, UnbakedModel> unbakedCache;

	@Shadow
	@Final
	private Map<ResourceLocation, UnbakedModel> topLevelModels;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadTopLevel(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 2))
	private void onInit(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profilerFiller, int i, CallbackInfo ci) {
		this.bossrifts$addModelToCache(iRiftRenderer.FRAME_MODEL);
		this.bossrifts$addModelToCache(iRiftRenderer.PEARL_MODEL);
	}

	@Unique
	private void bossrifts$addModelToCache(ResourceLocation location) {
		UnbakedModel unbakedmodel = this.getModel(location);
		this.unbakedCache.put(location, unbakedmodel);
		this.topLevelModels.put(location, unbakedmodel);
	}
}
