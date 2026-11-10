package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.ClientHooks;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ForgeClientHooksHelper implements IClientHooksHelper {

	@Override
	public BakedModel handleCameraTransforms(PoseStack matrixStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean leftHandHackery) {
		return ClientHooks.handleCameraTransforms(matrixStack, model, cameraTransformType, leftHandHackery);
	}

	@Override
	public boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
		return ClientHooks.isNameplateInRenderDistance(entity, squareDistance);
	}

	@Override
	public BakedModel getModel(ModelManager manager, ModelResourceLocation modelLocation) {
		return manager.getModel(modelLocation);
	}
}