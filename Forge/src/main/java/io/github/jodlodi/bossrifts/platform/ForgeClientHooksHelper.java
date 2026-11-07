package io.github.jodlodi.bossrifts.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.platform.services.IClientHooksHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.ForgeHooksClient;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ForgeClientHooksHelper implements IClientHooksHelper {

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public BakedModel handleCameraTransforms(PoseStack matrixStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean leftHandHackery) {
		return ForgeHooksClient.handleCameraTransforms(matrixStack, model, cameraTransformType, leftHandHackery);
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
		return ForgeHooksClient.isNameplateInRenderDistance(entity, squareDistance);
	}

	@Override
	public BakedModel getModel(ModelManager manager, ResourceLocation modelLocation) {
		return manager.getModel(modelLocation);
	}
}