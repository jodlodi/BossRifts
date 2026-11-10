package io.github.jodlodi.bossrifts.client.rift;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.mixin.accessor.ItemRendererAccessor;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class RiftItemRenderer implements iRiftRenderer<RiftItemRenderer.RenderInfo> {
	public static final Map<ItemStack, Pair<Long, Boolean>> STACK_MAP = new HashMap<>();
	private static final RiftItemRenderer INSTANCE = new RiftItemRenderer();
	private final ModelPart core;
	private final Map<Vec3i, ModelPart> shell = new HashMap<>();

	protected RiftItemRenderer() {
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					int u = z + y * 2;
					this.shell.put(
							new Vec3i(x * 2 - 1, y * 2 - 1, z * 2 - 1),
							build(iRiftRenderer.createShell(u, x, x, y, z))
					);
				}
			}
		}

		for (Direction direction : Direction.values()) {
			Vec3i vec = direction.getNormal();
			this.shell.put(vec, build(iRiftRenderer.createPin(direction.ordinal(), vec.getX(), vec.getY(), vec.getZ())));
		}

		this.core = build(iRiftRenderer.createCore());
	}

	public static void renderRift(ItemStack stack, RiftPearlItem pearl, ItemDisplayContext type, boolean leftHandHackery, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		float partialTick = Minecraft.getInstance().isPaused() ? 1.0F : Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		INSTANCE.preRender(new RenderInfo(stack, pearl, type, leftHandHackery), ms, buffer, light, overlay, partialTick);
	}

	public static ModelPart build(CubeListBuilder builder) {
		return new ModelPart(builder.getCubes().stream().map((definition) -> definition.bake(SHELL_WIDTH, SHELL_HEIGHT)).collect(ImmutableList.toImmutableList()), new HashMap<>());
	}

	@Override
	public ModelPart getCore() {
		return this.core;
	}

	@Override
	public Map<Vec3i, ModelPart> getShell() {
		return this.shell;
	}

	@Override
	public long getSeed(RenderInfo stack) {
		return stack.getPearl().getSeed(stack.getStack());
	}

	@Override
	public long getTime(RenderInfo renderInfo) {
		Entity entity = renderInfo.getStack().getEntityRepresentation();
		if (entity != null) {
			return entity.level().getGameTime();
		} else {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) return level.getGameTime();
		}
		return -1L;
	}

	@Override
	public long getPower(RenderInfo renderInfo) {
		return RiftPearlItem.poweredTime(renderInfo.getStack());
	}

	@Override
	public float getCharge(RenderInfo renderInfo) {
		if (renderInfo.getStack().getEntityRepresentation() instanceof Player player && player.isUsingItem() && player.getUseItem().equals(renderInfo.getStack())) {
			return RiftPearlItem.USE_DURATION - player.getUseItemRemainingTicks();
		}
		return -1L;
	}

	@Override
	public float getCharge0(RenderInfo renderInfo) {
		return this.getCharge(renderInfo) - 1;
	}

	@Override
	public float calculateScale(RenderInfo renderInfo, float exactTime) {
		if (renderInfo.getStack().getEntityRepresentation() instanceof Player player) {
			int price = renderInfo.getPearl().getPrice(
					player.blockPosition(),
					renderInfo.stack,
					RiftPearlItem.respawnPosition != null ? RiftPearlItem.respawnPosition : BlockPos.ZERO
			);

			if (price > -1) {
				boolean open = player.experienceLevel >= price || player.getAbilities().instabuild;

				if (!STACK_MAP.containsKey(renderInfo.getStack())) {
					STACK_MAP.put(renderInfo.getStack(), Pair.of(player.level().getGameTime(), !open));
				} else if (open == STACK_MAP.get(renderInfo.getStack()).getSecond()) {
					STACK_MAP.put(renderInfo.getStack(), Pair.of(player.level().getGameTime(), !open));
				}

				float scale = 1.0F;
				long powered = STACK_MAP.get(renderInfo.getStack()).getFirst();
				float diff = exactTime - powered;

				if (diff > 0) {
					if (open) scale = AnimUtil.clamp(1.0F - (diff / RiftPearlItem.TRANSITION_TIME));
					else scale = AnimUtil.clamp(diff / RiftPearlItem.TRANSITION_TIME);
				}

				return scale;
			}
		}
		return iRiftRenderer.super.calculateScale(renderInfo, exactTime);
	}

	@Override
	public void render(RenderInfo renderInfo, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		if (renderInfo.isDynamic()) stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale, 0.0D);

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		stack.pushPose();
		BakedModel riftFrame = manager.getModel(iRiftRenderer.FRAME_MODEL);
		Services.CLIENT_HOOKS.handleCameraTransforms(stack, riftFrame, renderInfo.getType(), renderInfo.isLeftHandHackery());
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		VertexConsumer vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		Map<Vec3i, Quaternionf> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(renderInfo, shuffleScale);

		int i = 0;
		for (Map.Entry<Vec3i, ModelPart> entry : this.getShell().entrySet()) {
			stack.pushPose();

			if (pose != null) stack.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((exactTime + i++ * 2.5D) * 0.1D) * 0.2D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

			Vec3i vector3i = entry.getKey();
			ModelPart modelRenderer = entry.getValue();
			stack.translate(
					vector3i.getX() * multiplier,
					vector3i.getY() * multiplier,
					vector3i.getZ() * multiplier
			);
			modelRenderer.render(stack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
			stack.popPose();
		}

		stack.pushPose();
		stack.scale(portalSize, portalSize, portalSize);
		this.getCore().render(stack, buffer.getBuffer(RenderType.endPortal()), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		stack.popPose();

		stack.pushPose();
		BakedModel riftSprite = manager.getModel(iRiftRenderer.PEARL_MODEL);
		riftSprite = Services.CLIENT_HOOKS.handleCameraTransforms(stack, riftSprite, renderInfo.getType(), renderInfo.isLeftHandHackery());

		if (renderInfo.isDynamic()) {
			this.spin(stack,
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
			);
		} else pearlSize *= 0.75F;

		stack.scale(pearlSize, pearlSize, pearlSize);
		stack.translate(-0.5D, -0.5D, -0.5D);

		((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer()).invokeRenderModelLists(
				riftSprite,
				renderInfo.getStack(),
				0xf000f0,
				overlay,
				stack,
				ItemRenderer.getFoilBufferDirect(
						buffer,
						ItemBlockRenderTypes.getRenderType(renderInfo.getStack(), true),
						true,
						renderInfo.getStack().hasFoil()
				)
		);
		stack.popPose();
		stack.popPose();
	}

	public static class RenderInfo {
		private final ItemStack stack;
		private final RiftPearlItem pearl;
		private final ItemDisplayContext type;
		private final boolean leftHandHackery;

		private RenderInfo(ItemStack stack, RiftPearlItem pearl, ItemDisplayContext type, boolean leftHandHackery) {
			this.stack = stack;
			this.pearl = pearl;
			this.type = type;
			this.leftHandHackery = leftHandHackery;
		}

		public ItemStack getStack() {
			return this.stack;
		}

		public RiftPearlItem getPearl() {
			return this.pearl;
		}

		public ItemDisplayContext getType() {
			return this.type;
		}

		public boolean isDynamic() {
			return this.type != ItemDisplayContext.GUI && this.type != ItemDisplayContext.FIXED;
		}

		public boolean isLeftHandHackery() {
			return this.leftHandHackery;
		}
	}

}
