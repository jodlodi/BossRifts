package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@NullMarked
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

	@Shadow
	abstract UnbakedModel getModel(ResourceLocation modelLocation);

	@Shadow
	protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 1))
	private void onInit(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, CallbackInfo ci) {
		this.registerModelAndLoadDependencies(iRiftRenderer.FRAME_MODEL, this.getModel(iRiftRenderer.FRAME_MODEL.id()));
		this.registerModelAndLoadDependencies(iRiftRenderer.PEARL_MODEL, this.getModel(iRiftRenderer.PEARL_MODEL.id()));
	}
}
