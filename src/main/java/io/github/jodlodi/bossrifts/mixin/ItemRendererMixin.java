package io.github.jodlodi.bossrifts.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(ItemRenderer.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemRendererMixin {
	@Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
	private void onRender(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay, IBakedModel model, CallbackInfo ci) {
		if (!(stack.getItem() instanceof RiftPearlItem)) return;
		if (type == ItemCameraTransforms.TransformType.GUI || type == ItemCameraTransforms.TransformType.FIXED) return;
		RiftItemRenderer.renderRift(stack, (RiftPearlItem) stack.getItem(), type, leftHandHackery, ms, buffer, light, overlay);
		ci.cancel();
	}
}