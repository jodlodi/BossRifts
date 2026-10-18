package io.github.jodlodi.bossrifts.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.MathHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AnimUtil {
	public static boolean isFinished(double progress, double length) {
		return progress >= length;
	}

	public static boolean justFinished(double progress, double length) {
		return progress >= length && (progress - 1.0D) < length;
	}

	public static float clampScaled(float progress, float length) {
		return MathHelper.clamp(progress / length, 0.0F, 1.0F);
	}

	public static float clampScaled(double progress, double length) {
		return MathHelper.clamp((float) (progress / length), 0.0F, 1.0F);
	}

	public static float scaled(float progress, float length) {
		return progress / length;
	}

	public static float scaled(double progress, double length) {
		return (float) (progress / length);
	}

	public static float clamp(float scale) {
		return MathHelper.clamp(scale, 0.0F, 1.0F);
	}

	public static float clamp(double scale) {
		return (float) MathHelper.clamp(scale, 0.0F, 1.0F);
	}
}
