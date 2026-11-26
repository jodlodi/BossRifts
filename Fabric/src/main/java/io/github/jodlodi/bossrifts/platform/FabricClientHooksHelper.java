package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.client.ModModelLoadingPlugin;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricClientHooksHelper implements IClientHooksHelper {
	private @Nullable QuadCollection lastCollection;
	private ItemQuads lastQuads = ItemQuads.EMPTY;

	@Override
	public void renderEye(PoseStack stack, SubmitNodeCollector collector, ItemDisplayContext context, boolean hasFoil, int outlineColor) {
		QuadCollection eyeModel = Minecraft.getInstance().getModelManager().getModel(ModModelLoadingPlugin.KEY);
		if (eyeModel == null) return;

		ItemQuads quads = this.quadsFor(eyeModel);
		if (quads.isEmpty()) return;

		collector.submitItem(
				stack,
				context,
				0xF000F0,
				OverlayTexture.NO_OVERLAY,
				outlineColor,
				new int[0],
				quads,
				hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE
		);
	}

	private ItemQuads quadsFor(QuadCollection collection) {
		if (collection != this.lastCollection) {
			this.lastCollection = collection;
			this.lastQuads = ItemQuads.split(collection.getAll());
		}
		return this.lastQuads;
	}
}