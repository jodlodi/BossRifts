package io.github.jodlodi.bossrifts.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IClientHooksHelper {

	void renderShields(PoseStack stack, SubmitNodeCollector collector);
}