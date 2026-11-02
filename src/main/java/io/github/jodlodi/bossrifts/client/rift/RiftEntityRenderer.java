package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RiftEntityRenderer extends EntityRenderer<BossRiftEntity> implements iRiftRenderer<BossRiftEntity> {
	private final ModelRenderer core;
	private final Map<Vector3i, ModelRenderer> shell = new HashMap<>();

	public RiftEntityRenderer(EntityRendererManager rendererManager) {
		super(rendererManager);
		this.shadowRadius = 0.25F;

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

	@Override
	public ResourceLocation getTextureLocation(BossRiftEntity rift) {
		return EndPortalTileEntityRenderer.END_PORTAL_LOCATION;
	}

	@Override
	public void render(BossRiftEntity rift, float angle, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		this.preRender(rift, stack, buffer, light, OverlayTexture.NO_OVERLAY, partialTick);
		super.render(rift, angle, partialTick, stack, buffer, light);
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
	public long getSeed(BossRiftEntity rift) {
		return rift.getSeed(rift);
	}

	@Override
	public long getTime(BossRiftEntity rift) {
		return rift.level.getGameTime();
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
	public void render(BossRiftEntity rift, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale + 0.25D, 0.0D);

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		stack.pushPose();
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		IVertexBuilder vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		Map<Vector3i, Quaternion> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(rift, shuffleScale);

		int i = 0;
		for (Map.Entry<Vector3i, ModelRenderer> entry : this.getShell().entrySet()) {
			stack.pushPose();

			if (pose != null) stack.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((exactTime + i++ * 2.5D) * 0.1D) * 0.2D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

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

		this.spin(stack,
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
				MathHelper.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
		);
		stack.scale(pearlSize * 0.85F, pearlSize * 0.85F, pearlSize * 0.85F);
		stack.translate(-0.5D, -0.5D, -0.5D);

		Minecraft.getInstance().getItemRenderer().renderModelLists(
				riftSprite,
				rift.getItem(),
				0xf000f0,
				overlay,
				stack,
				ItemRenderer.getFoilBufferDirect(
						buffer,
						RenderTypeLookup.getRenderType(rift.getItem(), true),
						true,
						rift.getItem().hasFoil()
				)
		);
		stack.popPose();
		stack.popPose();

		if (RiftConfig.isV1() && Minecraft.getInstance().hitResult instanceof EntityRayTraceResult && Minecraft.getInstance().player != null) {
			EntityRayTraceResult result = (EntityRayTraceResult) Minecraft.getInstance().hitResult;
			if (rift.equals(result.getEntity())) {
				int price = rift.getPrice(
						rift.blockPosition(),
						rift,
						RiftPearlItem.respawnPosition != null ? RiftPearlItem.respawnPosition : BlockPos.ZERO
				);

				if (price > -1) {
					this.renderCost(rift, new TranslationTextComponent(BossRiftEntity.COST, new StringTextComponent(String.valueOf(price)).withStyle(TextFormatting.GREEN)), stack, buffer, light);
				}
			}
		}
	}

	protected void renderCost(BossRiftEntity rift, ITextComponent component, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		if (ForgeHooksClient.isNameplateInRenderDistance(rift, this.entityRenderDispatcher.distanceToSqr(rift))) {
			stack.pushPose();

			stack.translate(0.0D, rift.getBbHeight() + 0.75F, 0.0D);
			stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			stack.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = stack.last().pose();
			int opacity = (int)(0.85 * 255.0F) << 24;
			FontRenderer fontrenderer = this.getFont();
			float f2 = (float)(-fontrenderer.width(component) / 2);
			fontrenderer.drawInBatch(component, f2, 0.0F, 553648127, false, matrix4f, buffer, true, opacity, light);
			fontrenderer.drawInBatch(component, f2, 0.0F, -1, false, matrix4f, buffer, false, 0, light);

			stack.popPose();
		}
	}
}