package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricClientHooksHelper implements IClientHooksHelper {

	@Override
	public BakedModel handleCameraTransforms(PoseStack matrixStack, BakedModel model, ItemTransforms.TransformType cameraTransformType, boolean leftHandHackery) {
		model.getTransforms().getTransform(cameraTransformType).apply(leftHandHackery, matrixStack);
		return model;
	}

	@Override
	public boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
		return squareDistance <= 4096.0D;
	}

	@Override
	public BakedModel getModel(ModelManager manager, ResourceLocation modelLocation) {
		BakedModel model = BakedModelManagerHelper.getModel(manager, modelLocation);
		if (model == null) model = manager.getModel(ModelBakery.MISSING_MODEL_LOCATION);
		return model;
	}
}