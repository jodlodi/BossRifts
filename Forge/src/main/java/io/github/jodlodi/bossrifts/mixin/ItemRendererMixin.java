package io.github.jodlodi.bossrifts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
	private void onRender(ItemStack stack, ItemTransforms.TransformType type, boolean leftHandHackery, PoseStack ms, MultiBufferSource buffer, int light, int overlay, BakedModel p_115151_, CallbackInfo ci) {
		if (!(stack.getItem() instanceof RiftPearlItem) || Services.CONFIG.isV1()) return;
		RiftItemRenderer.renderRift(stack, (RiftPearlItem) stack.getItem(), type, leftHandHackery, ms, buffer, light, overlay);
		ci.cancel();
	}
}