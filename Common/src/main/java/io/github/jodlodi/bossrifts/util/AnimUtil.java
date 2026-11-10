package io.github.jodlodi.bossrifts.util;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AnimUtil {
	public static boolean isFinished(double progress, double length) {
		return progress >= length;
	}

	public static boolean justFinished(double progress, double length) {
		return progress >= length && (progress - 1.0D) < length;
	}

	public static float clampScaled(float progress, float length) {
		return Mth.clamp(progress / length, 0.0F, 1.0F);
	}

	public static float clampScaled(double progress, double length) {
		return Mth.clamp((float) (progress / length), 0.0F, 1.0F);
	}

	public static float scaled(float progress, float length) {
		return progress / length;
	}

	public static float scaled(double progress, double length) {
		return (float) (progress / length);
	}

	public static float clamp(float scale) {
		return Mth.clamp(scale, 0.0F, 1.0F);
	}

	public static float clamp(double scale) {
		return (float) Mth.clamp(scale, 0.0F, 1.0F);
	}

	public static double random(RandomSource random, double min, double max) {
		return random.nextDouble() * (max - min) + min;
	}

	public static int minutesToTicks(int minutes) {
		return minutes * 60 * 20;
	}
}
