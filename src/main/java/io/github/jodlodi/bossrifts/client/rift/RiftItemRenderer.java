package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.AnimUtil;
import io.github.jodlodi.bossrifts.RiftCubeSolver;
import io.github.jodlodi.bossrifts.items.RiftPearl;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftItemRenderer implements iRift {
	private static final RiftItemRenderer INSTANCE = new RiftItemRenderer();
	private final ModelRenderer core;
	private final Map<Vector3i, ModelRenderer> shell = new HashMap<>();

	protected RiftItemRenderer() {
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					int u = z + y * 2;
					this.shell.put(
							new Vector3i(x * 2 - 1, y * 2 - 1, z * 2 - 1),
							this.createShell(u, x, x, y, z)
					);
				}
			}
		}

		for (Direction direction : Direction.values()) {
			Vector3i vec = direction.getNormal();
			this.shell.put(vec, this.createPin(direction.ordinal(), vec.getX(), vec.getY(), vec.getZ()));
		}

		this.core = this.createCore();
	}

	public static void renderRift(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		INSTANCE.render(stack, type, leftHandHackery, ms, buffer, light, overlay);
	}

	protected void render(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		RANDOM.setSeed(31100L);
		float partialTick = Minecraft.getInstance().isPaused() ? 0.0F : Minecraft.getInstance().getFrameTime();

		float longDelta = this.longDelta();
		long time = -1L;
		float ticksUsed = -1.0F;

		Entity entityRepresentation = stack.getEntityRepresentation();
		if (entityRepresentation != null) {
			longDelta = entityRepresentation.getUUID().getMostSignificantBits() % 1000 + entityRepresentation.tickCount + partialTick;

			int i = 0;
			for (ItemStack item : entityRepresentation.getAllSlots()) {
				if (item.sameItemStackIgnoreDurability(stack)) longDelta += i * -90.0F;
				i++;
			}

			time = entityRepresentation.level.getGameTime();

			if (entityRepresentation instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entityRepresentation;
				if (player.isUsingItem() && ItemStack.isSame(player.getUseItem(), stack)) {
					ticksUsed = RiftPearl.USE_DURATION - player.getUseItemRemainingTicks() + partialTick;
				}
			}
		} else {
			ClientWorld level = Minecraft.getInstance().level;
			if (level != null) time = level.getGameTime();
		}

		float scale = 1.0F;
		long powered = RiftPearl.poweredTime(stack);
		long longDiff = time - powered;
		float diff = longDiff + partialTick;

		if (time >= 0 && powered >= 0 && diff > 0) {
			if (diff <= RiftPearl.EMPOWER_DURATION) {
				scale = MathHelper.clamp(1.0F - (diff / RiftPearl.TRANSITION_TIME), 0.0F, 1.0F);
			} else {
				diff -= RiftPearl.EMPOWER_DURATION;
				scale = MathHelper.clamp(diff / RiftPearl.TRANSITION_TIME, 0.0F, 1.0F);
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

		float shuffleScale = AnimUtil.clampScaled(ticksUsed , RiftPearl.USE_DURATION - RiftPearl.COMPRESS_OFFSET);
		float compressScale = AnimUtil.clampScaled(ticksUsed , RiftPearl.USE_DURATION - RiftPearl.SHUFFLE_OFFSET);
		float useScale = ticksUsed >= 0.0F ? AnimUtil.clamp((compressScale - Math.pow(compressScale, 128.0D)) * 1.5F + 1.0F - compressScale) : 1.0F;
		float spinBonus = 1.0F + shuffleScale * shuffleScale * 1.25F;

		ms.pushPose();
		ms.translate(0.0D, Math.cos(longDelta * 0.125D) * -0.025F * scale, 0.0D);

		if (this.shell.isEmpty()) {
			for (int x = 0; x <= 1; x++) {
				for (int y = 0; y <= 1; y++) {
					for (int z = 0; z <= 1; z++) {
						int u = z + y * 2;
						Vector3i key = new Vector3i(x * 2 - 1, y * 2 - 1, z * 2 - 1);
						this.shell.put(key, this.createShell(u, x, x, y, z));
					}
				}
			}

			for (Direction direction : Direction.values()) {
				Vector3i vec = direction.getNormal();
				this.shell.put(vec, this.createPin(direction.ordinal(), vec.getX(), vec.getY(), vec.getZ()));
			}
		}

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		ms.pushPose();
		IBakedModel riftFrame = manager.getModel(RiftPearl.ENTITY_MODEL);
		ForgeHooksClient.handleCameraTransforms(ms, riftFrame, type, leftHandHackery);
		ms.scale(1.5F, 1.5F, 1.5F);

		this.spin(ms,
				MathHelper.wrapDegrees(longDelta * 0.125F * spinBonus),
				MathHelper.wrapDegrees(longDelta * 0.375F * spinBonus),
				MathHelper.wrapDegrees(longDelta * 0.625F * spinBonus)
		);

		IVertexBuilder vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		Map<Vector3i, Quaternion> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(stack, shuffleScale);

		int i = 0;
		for (Map.Entry<Vector3i, ModelRenderer> entry : this.shell.entrySet()) {
			ms.pushPose();

			if (pose != null) ms.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((longDelta + i++ * 2.5D) * 0.1D) * 0.1D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

			Vector3i vector3i = entry.getKey();
			ModelRenderer modelRenderer = entry.getValue();
			ms.translate(
					vector3i.getX() * multiplier,
					vector3i.getY() * multiplier,
					vector3i.getZ() * multiplier
			);
			modelRenderer.render(ms, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
			ms.popPose();
		}

		ms.pushPose();
		ms.scale(portalSize, portalSize, portalSize);
		for (int j = 0; j < 15; ++j) {
			float multi;
			if (j == 0) multi = 0.15F;
			else multi = 2.0F / (float) (18 - j);
			float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * multi;
			float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * multi;
			float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * multi;
			this.core.render(ms, buffer.getBuffer(RENDER_TYPES.get(j)), light, OverlayTexture.NO_OVERLAY, f, f1, f2, 1);
		}
		ms.popPose();
		ms.popPose();

		ms.pushPose();
		IBakedModel riftSprite = manager.getModel(RiftPearl.EYE_MODEL);
		riftSprite = ForgeHooksClient.handleCameraTransforms(ms, riftSprite, type, leftHandHackery);

		this.spin(ms,
				MathHelper.wrapDegrees(longDelta * -0.375F * spinBonus),
				MathHelper.wrapDegrees(longDelta * -0.625F * spinBonus),
				MathHelper.wrapDegrees(longDelta * -0.125F * spinBonus)
		);
		ms.scale(pearlSize, pearlSize, pearlSize);
		ms.translate(-0.5D, -0.5D, -0.5D);

		Minecraft.getInstance().getItemRenderer().renderModelLists(
				riftSprite,
				stack,
				0xf000f0,
				overlay,
				ms,
				ItemRenderer.getFoilBufferDirect(
						buffer,
						RenderTypeLookup.getRenderType(stack, true),
						true,
						stack.hasFoil()
				)
		);
		ms.popPose();
		ms.popPose();
	}

	private Map<Vector3i, Quaternion> computePose(ItemStack stack, float timeScale) {
		Map<Vector3i, Quaternion> pose = new HashMap<>();
		for (Vector3i key : this.shell.keySet()) pose.put(key, new Quaternion(Quaternion.ONE));

		long seed = 3626523L;
		if (stack.getOrCreateTag().contains(RiftPearl.POSITION)) {
			seed = NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(RiftPearl.POSITION)).asLong();
		}

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(seed);

		for (int move = 0; move < plan.size(); move++) {
			float length = plan.end(move) - plan.start(move);
			float progress = MathHelper.clamp((timeScale - plan.start(move)) / length, 0.0F, 1.0F);
			if (progress <= 0.0F) break;
			float eased = progress * progress * (3.0F - 2.0F * progress);

			this.turnLayer(pose, RiftCubeSolver.normal(plan.face(move)), plan.degrees(move) * eased);
		}

		return pose;
	}

	private void turnLayer(Map<Vector3i, Quaternion> pose, int[] normal, float angle) {
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
