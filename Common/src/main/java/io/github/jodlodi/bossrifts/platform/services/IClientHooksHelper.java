package io.github.jodlodi.bossrifts.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IClientHooksHelper {

	BakedModel handleCameraTransforms(PoseStack matrixStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean leftHandHackery);

	boolean isNameplateInRenderDistance(Entity entity, double squareDistance);
}