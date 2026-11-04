package io.github.jodlodi.bossrifts.mixin.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@NullMarked
@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
	@Invoker("renderModelLists")
	void invokeRenderModelLists(BakedModel model, ItemStack itemStack, int light, int overlay, PoseStack stack, VertexConsumer consumer);
}
