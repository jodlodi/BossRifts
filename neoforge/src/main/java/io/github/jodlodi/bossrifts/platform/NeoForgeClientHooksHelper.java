package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import io.github.jodlodi.bossrifts.events.ClientSetupListener;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class NeoForgeClientHooksHelper implements IClientHooksHelper {

	@Override
	public void renderShields(PoseStack stack, SubmitNodeCollector collector) {
		QuadCollection shieldModel = Minecraft.getInstance().getModelManager().getStandaloneModel(ClientSetupListener.KEY);
		if (shieldModel == null) return;

		List<BakedQuad> quads = shieldModel.getAll();
		if (quads.isEmpty()) return;

		QuadInstance instance = new QuadInstance();

		stack.pushPose();

		collector.submitCustomGeometry(stack, Sheets.translucentItemSheet(), (pose, buffer) -> {
			for (BakedQuad quad : quads) {
				buffer.putBakedQuad(pose, quad, instance);
			}
		});

		stack.popPose();
	}
}