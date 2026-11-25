package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.events.ClientSetupListener;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class NeoForgeClientHooksHelper implements IClientHooksHelper {

	@Override
	public void renderEye(PoseStack stack, SubmitNodeCollector collector, ItemDisplayContext context, boolean hasFoil, int outlineColor) {
		QuadCollection eyeModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientSetupListener.KEY);
		if (eyeModel == null) return;

		List<BakedQuad> quads = eyeModel.getAll();
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
}