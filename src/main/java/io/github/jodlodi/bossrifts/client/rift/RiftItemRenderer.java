package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
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
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftItemRenderer implements iRiftRenderer<RiftItemRenderer.RenderInfo> {
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

	public static void renderRift(ItemStack stack, RiftPearlItem pearl, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		float partialTick = Minecraft.getInstance().isPaused() ? 0.0F : Minecraft.getInstance().getFrameTime();
		INSTANCE.preRender(new RenderInfo(stack, pearl, type, leftHandHackery), ms, buffer, light, overlay, partialTick);
	}

	@Override
	public ModelRenderer getCore() {
		return this.core;
	}

	@Override
	public Map<Vector3i, ModelRenderer> getShell() {
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
			return entity.level.getGameTime();
		} else {
			ClientWorld level = Minecraft.getInstance().level;
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
		Entity entity = renderInfo.getStack().getEntityRepresentation();
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (player.isUsingItem() && player.getUseItem().equals(renderInfo.getStack())) {
				return RiftPearlItem.USE_DURATION - player.getUseItemRemainingTicks();
			}
		}
		return -1L;
	}

	@Override
	public float getCharge0(RenderInfo renderInfo) {
		return this.getCharge(renderInfo) - 1;
	}

	@Override
	public void render(RenderInfo renderInfo, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		if (renderInfo.isDynamic()) stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale, 0.0D);

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		stack.pushPose();
		IBakedModel riftFrame = manager.getModel(RiftPearlItem.ENTITY_MODEL);
		ForgeHooksClient.handleCameraTransforms(stack, riftFrame, renderInfo.getType(), renderInfo.isLeftHandHackery());
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		IVertexBuilder vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		Map<Vector3i, Quaternion> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(renderInfo, shuffleScale);

		int i = 0;
		for (Map.Entry<Vector3i, ModelRenderer> entry : this.getShell().entrySet()) {
			stack.pushPose();

			if (pose != null) stack.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((exactTime + i++ * 2.5D) * 0.1D) * 0.1D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

			Vector3i vector3i = entry.getKey();
			ModelRenderer modelRenderer = entry.getValue();
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
		for (int j = 0; j < 15; ++j) {
			float multi;
			if (j == 0) multi = 0.15F;
			else multi = 2.0F / (float) (18 - j);
			float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * multi;
			float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * multi;
			float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * multi;
			this.getCore().render(stack, buffer.getBuffer(RENDER_TYPES.get(j)), light, OverlayTexture.NO_OVERLAY, f, f1, f2, 1);
		}
		stack.popPose();
		stack.popPose();

		stack.pushPose();
		IBakedModel riftSprite = manager.getModel(RiftPearlItem.EYE_MODEL);
		riftSprite = ForgeHooksClient.handleCameraTransforms(stack, riftSprite, renderInfo.getType(), renderInfo.isLeftHandHackery());

		if (renderInfo.isDynamic()) {
			this.spin(stack,
					MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
					MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
					MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
			);
		} else pearlSize *= 0.75F;

		stack.scale(pearlSize, pearlSize, pearlSize);
		stack.translate(-0.5D, -0.5D, -0.5D);

		Minecraft.getInstance().getItemRenderer().renderModelLists(
				riftSprite,
				renderInfo.getStack(),
				0xf000f0,
				overlay,
				stack,
				ItemRenderer.getFoilBufferDirect(
						buffer,
						RenderTypeLookup.getRenderType(renderInfo.getStack(), true),
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
		private final ItemCameraTransforms.TransformType type;
		private final boolean leftHandHackery;

		private RenderInfo(ItemStack stack, RiftPearlItem pearl, ItemCameraTransforms.TransformType type, boolean leftHandHackery) {
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

		public ItemCameraTransforms.TransformType getType() {
			return this.type;
		}

		public boolean isDynamic() {
			return this.type != ItemCameraTransforms.TransformType.GUI && this.type != ItemCameraTransforms.TransformType.FIXED;
		}

		public boolean isLeftHandHackery() {
			return this.leftHandHackery;
		}
	}

}
