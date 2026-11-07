package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.mixin.accessor.ItemRendererAccessor;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.EntityHitResult;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class RiftEntityRenderer extends EntityRenderer<BossRiftEntity> implements iRiftRenderer<BossRiftEntity> {
	private final ModelPart core;
	private final Map<Vec3i, ModelPart> shell = new HashMap<>();

	public RiftEntityRenderer(EntityRendererProvider.Context rendererProvider) {
		super(rendererProvider);
		this.shadowRadius = 0.25F;

		ModelPart modelpart = rendererProvider.bakeLayer(BOSS_RIFT_MODEL);

		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					Vec3i vec3i = new Vec3i(x * 2 - 1, y * 2 - 1, z * 2 - 1);
					this.shell.put(vec3i, modelpart.getChild(iRiftRenderer.named("shell", vec3i)));
				}
			}
		}

		for (Direction direction : Direction.values()) {
			Vec3i vec = direction.getNormal();
			this.shell.put(vec, modelpart.getChild(iRiftRenderer.named("pin", vec)));
		}

		this.core = modelpart.getChild("core");
	}

	@Override
	public ResourceLocation getTextureLocation(BossRiftEntity rift) {
		return SHELL_LOCATION;
	}

	@Override
	public void render(BossRiftEntity rift, float angle, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		this.preRender(rift, stack, buffer, light, OverlayTexture.NO_OVERLAY, partialTick);
		super.render(rift, angle, partialTick, stack, buffer, light);
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
	public long getSeed(BossRiftEntity rift) {
		return rift.getSeed(rift);
	}

	@Override
	public long getTime(BossRiftEntity rift) {
		return rift.level().getGameTime();
	}

	@Override
	public long getPower(BossRiftEntity rift) {
		return RiftPearlItem.poweredTime(rift.getItem());
	}

	@Override
	public float getCharge(BossRiftEntity rift) {
		return rift.getPoints();
	}

	@Override
	public float getCharge0(BossRiftEntity rift) {
		return rift.getPoints0();
	}

	@Override
	public void render(BossRiftEntity rift, PoseStack stack, MultiBufferSource buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale + 0.25D, 0.0D);

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		stack.pushPose();
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		VertexConsumer vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		Map<Vec3i, Quaternionf> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(rift, shuffleScale);

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
		BakedModel riftSprite = Services.CLIENT_HOOKS.getModel(manager, iRiftRenderer.PEARL_MODEL);

		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
		);
		stack.scale(pearlSize * 0.85F, pearlSize * 0.85F, pearlSize * 0.85F);
		stack.translate(-0.5D, -0.5D, -0.5D);

		((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer()).invokeRenderModelLists(
				riftSprite,
				rift.getItem(),
				0xf000f0,
				overlay,
				stack,
				ItemRenderer.getFoilBufferDirect(
						buffer,
						ItemBlockRenderTypes.getRenderType(rift.getItem(), true),
						true,
						rift.getItem().hasFoil()
				)
		);
		stack.popPose();
		stack.popPose();

		if (Services.CONFIG.isV1() && Minecraft.getInstance().hitResult instanceof EntityHitResult result && Minecraft.getInstance().player != null) {
			if (rift.equals(result.getEntity())) {
				int price = rift.getPrice(
						rift.blockPosition(),
						rift,
						RiftPearlItem.respawnPosition != null ? RiftPearlItem.respawnPosition : BlockPos.ZERO
				);

				if (price > -1) {
					this.renderCost(rift, Component.translatable(BossRiftEntity.COST, Component.literal(String.valueOf(price)).withStyle(ChatFormatting.GREEN)), stack, buffer, light);
				}
			}
		}
	}

	protected void renderCost(BossRiftEntity rift, Component component, PoseStack stack, MultiBufferSource buffer, int light) {
		if (Services.CLIENT_HOOKS.isNameplateInRenderDistance(rift, this.entityRenderDispatcher.distanceToSqr(rift))) {
			stack.pushPose();
			stack.translate(0.0F, rift.getBbHeight() + 0.75F, 0.0F);
			stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			stack.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = stack.last().pose();
			int opacity = (int) (0.85 * 255.0F) << 24;
			Font font = this.getFont();
			float f2 = (float) (-font.width(component) / 2);
			font.drawInBatch(component, f2, 0.0F, 553648127, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, opacity, light);
			font.drawInBatch(component, f2, 0.0F, -1, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, light);

			stack.popPose();
		}
	}
}