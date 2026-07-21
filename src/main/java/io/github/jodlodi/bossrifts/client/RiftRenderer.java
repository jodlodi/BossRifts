package io.github.jodlodi.bossrifts.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RiftRenderer extends EntityRenderer<BossRiftEntity> {
	private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation(BossRifts.MOD_ID, "textures/entity/boss_rift/boss_rift.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
	@SuppressWarnings("UnstableApiUsage")
	private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.endPortal(i + 1)).collect(ImmutableList.toImmutableList());
	private static final Random RANDOM = new Random(31100L);
	private static final int TEXTURE_WIDTH = 64;
	private static final int TEXTURE_HEIGHT = 32;

	private final ModelRenderer core;
	private final Map<Vector3i, ModelRenderer> shell = new HashMap<>();

	public RiftRenderer(EntityRendererManager rendererManager) {
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

		this.core = new ModelRenderer(64, 32, 0, 0)
				.addBox(-3.9F, -3.9F, -3.9F, 7.8F, 7.8F, 7.8F);
	}

	protected ModelRenderer createShell(int u, int v, int x, int y, int z) {
		return new ModelRenderer(TEXTURE_WIDTH, TEXTURE_HEIGHT, u * 16, v * 8)
				.addBox(-4 + x * 4, -4 + y * 4, -4 + z * 4, 4, 4, 4);
	}

	protected ModelRenderer createPin(int u, int x, int y, int z) {
		return new ModelRenderer(TEXTURE_WIDTH, TEXTURE_HEIGHT, u * 8, 16)
				.addBox(-1 + x * 3, -1 + y * 3, -1 + z * 3, 2, 2, 2);
	}

	@Override
	public ResourceLocation getTextureLocation(BossRiftEntity rift) {
		return EndPortalTileEntityRenderer.END_PORTAL_LOCATION;
	}

	@Override
	public void render(BossRiftEntity rift, float angle, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		RANDOM.setSeed(31100L);
		stack.pushPose();
		double multiplier = (double) rift.getLerpedPoints(partialTick) / (double) BossRiftEntity.WARP_SPAN / 20.0D;

		stack.translate(0, 0.25D, 0);
		stack.mulPose(Vector3f.YP.rotationDegrees(rift.rot));

		for (int i = 0; i < 15; ++i) {
			float multi;
			if (i == 0) multi = 0.15F;
			else multi = 2.0F / (float) (18 - i);
			float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * multi;
			float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * multi;
			float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * multi;
			this.core.render(stack, buffer.getBuffer(RENDER_TYPES.get(i)), light, OverlayTexture.NO_OVERLAY, f, f1, f2, 1);
		}

		IVertexBuilder vertexBuilder = buffer.getBuffer(RENDER_TYPE);

		this.shell.forEach((vector3i, modelRenderer) -> {
			stack.pushPose();
			stack.translate(
					vector3i.getX() * multiplier,
					vector3i.getY() * multiplier,
					vector3i.getZ() * multiplier
			);
			modelRenderer.render(stack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
			stack.popPose();
		});

		stack.popPose();
		super.render(rift, angle, partialTick, stack, buffer, light);
	}
}