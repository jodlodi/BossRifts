package io.github.jodlodi.bossrifts.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RiftAggregatorRenderer implements BlockEntityRenderer<RiftAggregatorBlockEntity> {
	public static ModelLayerLocation AGGREGATOR_RIFT_MODEL = new ModelLayerLocation(Constants.prefix("aggregator_rift"), "main");
	private final ModelPart core;

	public RiftAggregatorRenderer(BlockEntityRendererProvider.Context context) {
		this.core = context.bakeLayer(AGGREGATOR_RIFT_MODEL).getChild("core");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("core", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(2.0F, 13.0F, 2.0F, 12.0F, 2.0F, 12.0F), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, iRiftRenderer.SHELL_WIDTH, iRiftRenderer.SHELL_HEIGHT);
	}

	@Override
	public void render(RiftAggregatorBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		this.core.render(stack, buffer.getBuffer(RenderType.endPortal()), light, OverlayTexture.NO_OVERLAY);
	}
}
