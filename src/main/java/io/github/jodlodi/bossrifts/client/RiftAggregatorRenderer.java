package io.github.jodlodi.bossrifts.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftAggregatorRenderer extends TileEntityRenderer<RiftAggregatorBlockEntity> {
	@SuppressWarnings("UnstableApiUsage")
	private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.endPortal(i + 1)).collect(ImmutableList.toImmutableList());
	private static final Random RANDOM = new Random(31100L);

	private final ModelRenderer core;

	public RiftAggregatorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);

		this.core = new ModelRenderer(64, 32, 0, 0)
				.addBox(2.0F, 13.0F, 2.0F, 12.0F, 2.0F, 12.0F);
	}

	@Override
	public void render(RiftAggregatorBlockEntity entity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		RANDOM.setSeed(31100L);

		stack.pushPose();

		for (int i = 0; i < 15; ++i) {
			float multi;
			if (i == 0) multi = 0.15F;
			else multi = 2.0F / (float) (18 - i);
			float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * multi;
			float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * multi;
			float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * multi;
			this.core.render(stack, buffer.getBuffer(RENDER_TYPES.get(i)), combinedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1);
		}

		stack.popPose();
	}
}
