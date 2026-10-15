package io.github.jodlodi.bossrifts.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.client.iRiftRenderer;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@Mixin(ItemRenderer.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemRendererMixin implements iRiftRenderer {
	private static final @Unique float bossRifts$ANIMATION_DURATION = 70.0F;
	private final @Unique ModelRenderer bossRifts$core = new ModelRenderer(64, 32, 0, 0)
			.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
	private final @Unique Map<Vector3i, ModelRenderer> bossRifts$shell = new HashMap<>();
	private final @Unique Map<Direction, List<Vector3i>> bossRifts$directionMap = new HashMap<>();

	@Shadow
	public abstract void renderModelLists(IBakedModel model, ItemStack stack, int light, int overlay, MatrixStack ms, IVertexBuilder builder);

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
	private void onRender(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay, IBakedModel model, CallbackInfo ci) {
		if (!(stack.getItem() instanceof RiftPearl)) return;
		if (type == ItemCameraTransforms.TransformType.GUI || type == ItemCameraTransforms.TransformType.FIXED) return;

		RANDOM.setSeed(31100L);
		float partialTick = Minecraft.getInstance().isPaused() ? 0.0F : Minecraft.getInstance().getFrameTime();

		ci.cancel();
		float longDelta = this.longDelta();
		long time = -1L;

		Entity entityRepresentation = stack.getEntityRepresentation();
		if (entityRepresentation != null) {
			longDelta = entityRepresentation.getUUID().getMostSignificantBits() % 1000 + entityRepresentation.tickCount + partialTick;

			int i = 0;
			for (ItemStack item : entityRepresentation.getAllSlots()) {
				if (item.sameItemStackIgnoreDurability(stack)) longDelta += i * -90.0F;
				i++;
			}

			time = entityRepresentation.level.getGameTime();
		} else {
			ClientWorld level = Minecraft.getInstance().level;
			if (level != null) time = level.getGameTime();
		}

		float scale = 1.0F;
		long powered = stack.hasTag() && stack.getOrCreateTag().contains(RiftPearl.POWERED) ? stack.getOrCreateTag().getLong(RiftPearl.POWERED) : -1L;
		long longDiff = time - powered;
		float diff = longDiff + partialTick;

		if (time >= 0 && powered >= 0 && diff > 0) {
			if (diff <= RiftPearl.EMPOWER_DURATION) {
				scale = MathHelper.clamp(1.0F - (diff / bossRifts$ANIMATION_DURATION), 0.0F, 1.0F);
			} else {
				diff -= RiftPearl.EMPOWER_DURATION;
				scale = MathHelper.clamp(diff / bossRifts$ANIMATION_DURATION, 0.0F, 1.0F);
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
		float pearlRot = antiScale <= 0.5 ? 1.0F - sinScale : 0.0F;
		float portalRot = scale <= 0.5 ? 1.0F - sinScale : 0.0F;

		ms.pushPose();
		ms.translate(0.0D, Math.cos(longDelta * 0.125D) * -0.025F * scale, 0.0D);

		if (this.bossRifts$shell.isEmpty()) {
			for (int x = 0; x <= 1; x++) {
				for (int y = 0; y <= 1; y++) {
					for (int z = 0; z <= 1; z++) {
						int u = z + y * 2;
						Vector3i key = new Vector3i(x * 2 - 1, y * 2 - 1, z * 2 - 1);
						this.bossRifts$shell.put(key, this.createShell(u, x, x, y, z));

						for (Direction direction : Direction.values()) {
							switch (direction) {
								case DOWN:
									if (y == 0) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.DOWN, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.DOWN, list);
									}
									break;
								case UP:
									if (y == 1) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.UP, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.UP, list);
									}
									break;
								case NORTH:
									if (z == 0) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.NORTH, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.NORTH, list);
									}
									break;
								case SOUTH:
									if (z == 1) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.SOUTH, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.SOUTH, list);
									}
									break;
								case WEST:
									if (x == 0) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.WEST, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.WEST, list);
									}
									break;
								case EAST:
									if (x == 1) {
										List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(Direction.EAST, new ArrayList<>());
										list.add(key);
										this.bossRifts$directionMap.put(Direction.EAST, list);
									}
									break;
							}
						}
					}
				}
			}

			for (Direction direction : Direction.values()) {
				Vector3i vec = direction.getNormal();
				this.bossRifts$shell.put(vec, this.createPin(direction.ordinal(), vec.getX(), vec.getY(), vec.getZ()));
				List<Vector3i> list = this.bossRifts$directionMap.getOrDefault(direction, new ArrayList<>());
				list.add(vec);
				this.bossRifts$directionMap.put(direction, list);
			}
		}

		ModelManager manager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();

		ms.pushPose();
		IBakedModel riftFrame = manager.getModel(RiftPearl.ENTITY_MODEL);
		ForgeHooksClient.handleCameraTransforms(ms, riftFrame, type, leftHandHackery);
		ms.scale(1.5F, 1.5F, 1.5F);

		this.spin(ms, longDelta, 0.125F, 0.375F, 0.625F, 1);

		IVertexBuilder vertexBuilder = buffer.getBuffer(SHELL_TYPE);

		int i = 0;
		for (Map.Entry<Vector3i, ModelRenderer> entry : this.bossRifts$shell.entrySet()) {
			ms.pushPose();
/*
			float fara = 2.0F;
			if (this.bossRifts$directionMap.get(Direction.DOWN).contains(entry.getKey())) {
				this.spin(ms, diff, 0.0F, fara, 0.0F, portalRot);
			} else if (this.bossRifts$directionMap.get(Direction.UP).contains(entry.getKey())) {
				this.spin(ms, diff, 0.0F, -fara, 0.0F, portalRot);
			}*/

			double multiplier = ((Math.sin((longDelta + i++ * 2.5D) * 0.2D) * 0.2D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close;

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
			this.bossRifts$core.render(ms, buffer.getBuffer(RENDER_TYPES.get(j)), light, OverlayTexture.NO_OVERLAY, f, f1, f2, 1);
		}
		ms.popPose();
		ms.popPose();

		ms.pushPose();
		IBakedModel riftSprite = manager.getModel(RiftPearl.EYE_MODEL);
		riftSprite = ForgeHooksClient.handleCameraTransforms(ms, riftSprite, type, leftHandHackery);

		this.spin(ms, longDelta, -0.125F, -0.375F, -0.625F, 1);
		ms.scale(pearlSize, pearlSize, pearlSize);
		ms.translate(-0.5D, -0.5D, -0.5D);

		this.renderModelLists(
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
}
