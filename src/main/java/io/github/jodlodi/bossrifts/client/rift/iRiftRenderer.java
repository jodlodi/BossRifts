package io.github.jodlodi.bossrifts.client.rift;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface iRiftRenderer<T> {
	ResourceLocation SHELL_LOCATION = new ResourceLocation(BossRifts.MOD_ID, "textures/entity/boss_rift/boss_rift.png");
	RenderType SHELL_TYPE = RenderType.entityCutoutNoCull(SHELL_LOCATION);
	int SHELL_WIDTH = 64;
	int SHELL_HEIGHT = 32;

	@SuppressWarnings("UnstableApiUsage")
	List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.endPortal(i + 1)).collect(ImmutableList.toImmutableList());
	Random RANDOM = new Random(31100L);

	ModelRenderer getCore();

	Map<Vector3i, ModelRenderer> getShell();

	default ModelRenderer createCore() {
		return new ModelRenderer(64, 32, 0, 0)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
	}

	default ModelRenderer createShell(int u, int v, int x, int y, int z) {
		return new ModelRenderer(SHELL_WIDTH, SHELL_HEIGHT, u * 16, v * 8)
				.addBox(-4 + x * 4, -4 + y * 4, -4 + z * 4, 4, 4, 4);
	}

	default ModelRenderer createPin(int u, int x, int y, int z) {
		return new ModelRenderer(SHELL_WIDTH, SHELL_HEIGHT, u * 8, 16)
				.addBox(-1 + x * 3, -1 + y * 3, -1 + z * 3, 2, 2, 2);
	}

	default void spin(MatrixStack ms, float x, float y, float z) {
		ms.mulPose(Vector3f.XP.rotationDegrees(x));
		ms.mulPose(Vector3f.YP.rotationDegrees(y));
		ms.mulPose(Vector3f.ZP.rotationDegrees(z));
	}

	long getSeed(T t);

	long getTime(T t);

	long getPower(T t);

	float getCharge(T t);

	float getCharge0(T t);

	default void preRender(T t, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay, float partialTick) {
		RANDOM.setSeed(31100L);
		float exactTime = this.getTime(t) + partialTick;
		float charge = MathHelper.lerp(partialTick, this.getCharge0(t), this.getCharge(t));

		float scale = 1.0F;
		long powered = this.getPower(t);
		float diff = exactTime - powered;

		if (powered >= 0 && diff > 0) {
			if (diff <= RiftPearlItem.EMPOWER_DURATION) {
				scale = MathHelper.clamp(1.0F - (diff / RiftPearlItem.TRANSITION_TIME), 0.0F, 1.0F);
			} else {
				diff -= RiftPearlItem.EMPOWER_DURATION;
				scale = MathHelper.clamp(diff / RiftPearlItem.TRANSITION_TIME, 0.0F, 1.0F);
			}
		}

		double scalar = 3.0D;
		double cut = 1.05D;

		float antiScale = 1.0F - scale;
		float sinScale = (float) Math.sin(scale * Math.PI);
		float close = (float) Math.pow(MathHelper.clamp(1.0D - sinScale * cut, 0.0D, 1.0D), 1.0 / scalar);
		float shrink = (float) Math.pow(MathHelper.clamp(1.0D - sinScale, 0.0D, 1.0D), 1.0D / (scalar * scalar * scalar));

		float pearlSize = antiScale <= 0.5 ? shrink : 0.0F;
		float portalSize = scale <= 0.5 ? shrink : 0.0F;

		float shuffleScale = AnimUtil.clampScaled(charge , RiftPearlItem.USE_DURATION - RiftPearlItem.COMPRESS_OFFSET);
		float compressScale = AnimUtil.clampScaled(charge , RiftPearlItem.USE_DURATION - RiftPearlItem.SHUFFLE_OFFSET);
		float useScale = charge >= 0.0F ? AnimUtil.clamp((compressScale - Math.pow(compressScale, 128.0D)) * 1.5F + 1.0F - compressScale) : 1.0F;
		float spinBonus = shuffleScale * shuffleScale * 1800.0F;

		this.render(t, stack, buffer, light, overlay, exactTime, scale, antiScale, close, pearlSize, portalSize, shuffleScale, useScale, spinBonus);
	}

	void render(T t, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus);

	default Map<Vector3i, Quaternion> computePose(T t, float timeScale) {
		Map<Vector3i, Quaternion> pose = new HashMap<>();
		for (Vector3i key : this.getShell().keySet()) pose.put(key, new Quaternion(Quaternion.ONE));

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(this.getSeed(t));

		for (int move = 0; move < plan.size(); move++) {
			float length = plan.end(move) - plan.start(move);
			float progress = MathHelper.clamp((timeScale - plan.start(move)) / length, 0.0F, 1.0F);
			if (progress <= 0.0F) break;
			float eased = progress * progress * (3.0F - 2.0F * progress);

			this.turnLayer(pose, RiftCubeSolver.normal(plan.face(move)), plan.degrees(move) * eased);
		}

		return pose;
	}

	default void turnLayer(Map<Vector3i, Quaternion> pose, int[] normal, float angle) {
		Quaternion turn = new Quaternion(new Vector3f(normal[0], normal[1], normal[2]), angle, true);

		for (Map.Entry<Vector3i, Quaternion> entry : pose.entrySet()) {
			Vector3i key = entry.getKey();
			Vector3f pos = new Vector3f(key.getX(), key.getY(), key.getZ());
			pos.transform(entry.getValue());

			int dot = Math.round(pos.x()) * normal[0]
					+ Math.round(pos.y()) * normal[1]
					+ Math.round(pos.z()) * normal[2];

			if (dot == 1) {
				Quaternion q = new Quaternion(turn);
				q.mul(entry.getValue());
				entry.setValue(q);
			}
		}
	}
}
