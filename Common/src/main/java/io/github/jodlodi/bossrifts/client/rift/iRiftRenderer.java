package io.github.jodlodi.bossrifts.client.rift;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@NullMarked
public interface iRiftRenderer<T> {
	ModelLayerLocation BOSS_RIFT_MODEL = new ModelLayerLocation(Constants.prefix("boss_rift"), "main");
	ResourceLocation SHELL_LOCATION = Constants.prefix("textures/entity/boss_rift/boss_rift.png");
	ResourceLocation ENTITY_MODEL = Constants.prefix("item/rift_pearl_frame");
	ResourceLocation EYE_MODEL = Constants.prefix("item/rift_pearl_icon");
	RenderType SHELL_TYPE = RenderType.entityCutoutNoCull(SHELL_LOCATION);
	int SHELL_WIDTH = 64;
	int SHELL_HEIGHT = 32;

	ModelPart getCore();

	Map<Vec3i, ModelPart> getShell();

	static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("core", createCore(), PartPose.ZERO);

		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					int u = z + y * 2;
					partdefinition.addOrReplaceChild(named("shell", new Vec3i(x * 2 - 1, y * 2 - 1, z * 2 - 1)), createShell(u, x, x, y, z), PartPose.ZERO);
				}
			}
		}

		for (Direction direction : Direction.values()) {
			Vec3i vec = direction.getNormal();
			partdefinition.addOrReplaceChild(named("pin", vec), createPin(direction.ordinal(), vec.getX(), vec.getY(), vec.getZ()), PartPose.ZERO);
		}

		return LayerDefinition.create(meshdefinition, SHELL_WIDTH, SHELL_HEIGHT);
	}

	static String named(String id, Vec3i vec3i) {
		return id + "x" + vec3i.getX() + "y" + vec3i.getY() + "z" + vec3i.getZ();
	}

	static CubeListBuilder createCore() {
		return CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
	}

	static CubeListBuilder createShell(int u, int v, int x, int y, int z) {
		return CubeListBuilder.create()
				.texOffs(u * 16, v * 8)
				.addBox(-4 + x * 4, -4 + y * 4, -4 + z * 4, 4, 4, 4);
	}

	static CubeListBuilder createPin(int u, int x, int y, int z) {
		return CubeListBuilder.create()
				.texOffs( u * 8, 16)
				.addBox(-1 + x * 3, -1 + y * 3, -1 + z * 3, 2, 2, 2);
	}

	default void spin(PoseStack ms, float x, float y, float z) {
		ms.mulPose(Vector3f.XP.rotationDegrees(x));
		ms.mulPose(Vector3f.YP.rotationDegrees(y));
		ms.mulPose(Vector3f.ZP.rotationDegrees(z));
	}

	long getSeed(T t);

	long getTime(T t);

	long getPower(T t);

	float getCharge(T t);

	float getCharge0(T t);

	default void preRender(T t, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float partialTick) {
		float exactTime = this.getTime(t) + partialTick;
		float charge = Mth.lerp(partialTick, this.getCharge0(t), this.getCharge(t));

		float scale = 1.0F;
		long powered = this.getPower(t);
		float diff = exactTime - powered;

		if (powered >= 0 && diff > 0) {
			if (diff <= Services.CONFIG.empowerDuration()) {
				scale = AnimUtil.clamp(1.0F - (diff / RiftPearlItem.TRANSITION_TIME));
			} else {
				diff -= Services.CONFIG.empowerDuration();
				scale = AnimUtil.clamp(diff / RiftPearlItem.TRANSITION_TIME);
			}
		}

		if (charge >= 0.0F) scale = 0.0F;

		double scalar = 3.0D;
		double cut = 1.05D;

		float antiScale = 1.0F - scale;
		float sinScale = (float) Math.sin(scale * Math.PI);
		float close = (float) Math.pow(AnimUtil.clamp(1.0D - sinScale * cut), 1.0 / scalar);
		float shrink = (float) Math.pow(AnimUtil.clamp(1.0D - sinScale), 1.0D / (scalar * scalar * scalar));

		float pearlSize = antiScale <= 0.5 ? shrink : 0.0F;
		float portalSize = scale <= 0.5 ? shrink : 0.0F;

		float shuffleScale = AnimUtil.clampScaled(charge , RiftPearlItem.USE_DURATION - RiftPearlItem.COMPRESS_OFFSET);
		float compressScale = AnimUtil.clampScaled(charge , RiftPearlItem.USE_DURATION - RiftPearlItem.SHUFFLE_OFFSET);
		float useScale = charge >= 0.0F ? AnimUtil.clamp((compressScale - Math.pow(compressScale, 128.0D)) * 1.5F + 1.0F - compressScale) : 1.0F;
		float spinBonus = shuffleScale * shuffleScale * 1800.0F;

		this.render(t, stack, buffer, light, overlay, exactTime, scale, antiScale, close, pearlSize, portalSize, shuffleScale, useScale, spinBonus);
	}

	void render(T t, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus);

	default Map<Vec3i, Quaternion> computePose(T t, float timeScale) {
		Map<Vec3i, Quaternion> pose = new HashMap<>();
		for (Vec3i key : this.getShell().keySet()) pose.put(key, new Quaternion(Quaternion.ONE));

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(this.getSeed(t));

		for (int move = 0; move < plan.size(); move++) {
			float length = plan.end(move) - plan.start(move);
			float progress = AnimUtil.clamp((timeScale - plan.start(move)) / length);
			if (progress <= 0.0F) break;
			float eased = progress * progress * (3.0F - 2.0F * progress);

			this.turnLayer(pose, RiftCubeSolver.normal(plan.face(move)), plan.degrees(move) * eased);
		}

		return pose;
	}

	default void turnLayer(Map<Vec3i, Quaternion> pose, int[] normal, float angle) {
		Quaternion turn = new Quaternion(new Vector3f(normal[0], normal[1], normal[2]), angle, true);

		for (Map.Entry<Vec3i, Quaternion> entry : pose.entrySet()) {
			Vec3i key = entry.getKey();
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
