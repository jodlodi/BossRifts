package io.github.jodlodi.bossrifts.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IClientHooksHelper {

	void renderEye(PoseStack stack, SubmitNodeCollector collector, ItemDisplayContext context, boolean hasFoil, int outlineColor);
}