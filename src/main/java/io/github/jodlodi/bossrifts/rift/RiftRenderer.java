package io.github.jodlodi.bossrifts.rift;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RiftRenderer extends EntityRenderer<BossRiftEntity> {
    private static final ResourceLocation RIFT_LOCATION = new ResourceLocation(BossRifts.MOD_ID,"textures/entity/boss_rift/boss_rift.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(RIFT_LOCATION);
    private static final Random RANDOM = new Random(31100L);

    private final ModelPart core;

    private final ModelPart shellE1;
    private final ModelPart shellE2;
    private final ModelPart shellE3;
    private final ModelPart shellE4;
    private final ModelPart shellE5;
    private final ModelPart shellE6;
    private final ModelPart shellE7;
    private final ModelPart shellE8;

    private final ModelPart shellC1;
    private final ModelPart shellC2;
    private final ModelPart shellC3;
    private final ModelPart shellC4;
    private final ModelPart shellC5;
    private final ModelPart shellC6;

    public RiftRenderer(EntityRendererProvider.Context rendererProvider) {
        super(rendererProvider);
        this.shadowRadius = 0.25F;

        ModelPart modelpart = rendererProvider.bakeLayer(Reg.BOSS_RIFT_MODEL);
        this.core = modelpart.getChild("core");
        this.shellE1 = modelpart.getChild("shellE1");
        this.shellE2 = modelpart.getChild("shellE2");
        this.shellE3 = modelpart.getChild("shellE3");
        this.shellE4 = modelpart.getChild("shellE4");
        this.shellE5 = modelpart.getChild("shellE5");
        this.shellE6 = modelpart.getChild("shellE6");
        this.shellE7 = modelpart.getChild("shellE7");
        this.shellE8 = modelpart.getChild("shellE8");
        this.shellC1 = modelpart.getChild("shellC1");
        this.shellC2 = modelpart.getChild("shellC2");
        this.shellC3 = modelpart.getChild("shellC3");
        this.shellC4 = modelpart.getChild("shellC4");
        this.shellC5 = modelpart.getChild("shellC5");
        this.shellC6 = modelpart.getChild("shellC6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-3.9F, -3.9F, -3.9F, 7.8F, 7.8F, 7.8F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE1", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4F, -4F, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE2", CubeListBuilder.create().texOffs(16, 0).addBox(-4F, 0, -4F, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE3", CubeListBuilder.create().texOffs(32, 0).addBox(0, 0, -4F, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE4", CubeListBuilder.create().texOffs(48, 0).addBox(0, -4F, -4F, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE5", CubeListBuilder.create().texOffs(0, 8).addBox(-4F, -4F, 0, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE6", CubeListBuilder.create().texOffs(16, 8).addBox(-4F, 0, 0, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE7", CubeListBuilder.create().texOffs(32, 8).addBox(0, 0, 0, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellE8", CubeListBuilder.create().texOffs(48, 8).addBox(0, -4F, 0, 4F, 4F, 4F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC1", CubeListBuilder.create().texOffs(0, 16).addBox(-4F,-1F,-1F,2F,2F,2F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC2", CubeListBuilder.create().texOffs(8,16).addBox(2F,-1F,-1F,2F,2F,2F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC3", CubeListBuilder.create().texOffs(16,16).addBox(-1F,-4F,-1F,2F,2F,2F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC4", CubeListBuilder.create().texOffs(24,16).addBox(-1F,2F,-1F,2F,2F,2F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC5", CubeListBuilder.create().texOffs(32,16).addBox(-1F,-1F,-4F,2F,2F,2F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("shellC6", CubeListBuilder.create().texOffs(40,16).addBox(-1F,-1F,2F,2F,2F,2F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 20);
    }

    public ResourceLocation getTextureLocation(BossRiftEntity p_114482_) {
        return RIFT_LOCATION;
    }

    public void render(BossRiftEntity rift, float v, float v1, PoseStack stack, MultiBufferSource buffer, int light) {
        RANDOM.setSeed(31100L);
        stack.pushPose();
        double multiplier = (double)rift.getPoints() / (double)rift.warpSpan / 20;
        float spinn = rift.time / 2 + rift.revSpeed * 2F + v1;
        stack.pushPose();
        int i = OverlayTexture.NO_OVERLAY;

        stack.translate(0, 0.25D, 0);
        stack.mulPose(Vector3f.YP.rotationDegrees(spinn));
        this.core.render(stack, buffer.getBuffer(RenderType.endPortal()), light, i);

        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);

        stack.translate(-multiplier, -multiplier, -multiplier);
        this.shellE1.render(stack, vertexconsumer, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellE2.render(stack, vertexconsumer, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellE3.render(stack, vertexconsumer, light, i);
        stack.translate(0, multiplier * -2, 0);
        this.shellE4.render(stack, vertexconsumer, light, i);
        stack.translate(multiplier * -2, 0, multiplier * 2);
        this.shellE5.render(stack, vertexconsumer, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellE6.render(stack, vertexconsumer, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellE7.render(stack, vertexconsumer, light, i);
        stack.translate(0, multiplier * -2, 0);
        this.shellE8.render(stack, vertexconsumer, light, i);

        stack.translate(-multiplier * 2, multiplier, -multiplier);
        this.shellC1.render(stack, vertexconsumer, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellC2.render(stack, vertexconsumer, light, i);
        stack.translate(-multiplier, -multiplier, 0);
        this.shellC3.render(stack, vertexconsumer, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellC4.render(stack, vertexconsumer, light, i);
        stack.translate(0, -multiplier, -multiplier);
        this.shellC5.render(stack, vertexconsumer, light, i);
        stack.translate(0, 0, multiplier * 2);
        this.shellC6.render(stack, vertexconsumer, light, i);

        stack.popPose();
        stack.popPose();

        super.render(rift, v, v1, stack, buffer, light);
    }
}