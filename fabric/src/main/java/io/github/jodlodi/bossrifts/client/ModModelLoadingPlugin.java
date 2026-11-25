package io.github.jodlodi.bossrifts.client;

import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModModelLoadingPlugin implements ModelLoadingPlugin {
	public static final ExtraModelKey<QuadCollection> KEY = ExtraModelKey.create(iRiftRenderer.PEARL_ITEM_MODEL::toDebugFileName);

	@Override
	public void initialize(Context pluginContext) {
		pluginContext.addModel(KEY, new SimpleUnbakedExtraModel<>(iRiftRenderer.PEARL_ITEM_MODEL, (resolvedModel, modelBaker) ->
				resolvedModel.bakeTopGeometry(resolvedModel.getTopTextureSlots(), modelBaker, BlockModelRotation.IDENTITY)
		));
	}
}
