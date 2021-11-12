package io.github.jodlodi.bossrifts.rift;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.jodlodi.bossrifts.BossRifts;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class RiftRenderer extends EntityRenderer<BossRiftEntity> {
    private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation(BossRifts.MOD_ID,"textures/entity/boss_rift/boss_rift.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((p_228882_0_) -> RenderType.endPortal(p_228882_0_ + 1)).collect(ImmutableList.toImmutableList());
    private static final Random RANDOM = new Random(31100L);

    private final ModelRenderer core;

    private final ModelRenderer shellE1;
    private final ModelRenderer shellE2;
    private final ModelRenderer shellE3;
    private final ModelRenderer shellE4;
    private final ModelRenderer shellE5;
    private final ModelRenderer shellE6;
    private final ModelRenderer shellE7;
    private final ModelRenderer shellE8;

    private final ModelRenderer shellC1;
    private final ModelRenderer shellC2;
    private final ModelRenderer shellC3;
    private final ModelRenderer shellC4;
    private final ModelRenderer shellC5;
    private final ModelRenderer shellC6;

    public RiftRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.shadowRadius = 0.25F;

        this.shellE1 = new ModelRenderer(64, 20, 0, 0);
        this.shellE1.addBox(-4, -4, -4, 4, 4, 4);
        this.shellE2 = new ModelRenderer(64, 20, 16, 0);
        this.shellE2.addBox(-4, 0, -4, 4, 4, 4);
        this.shellE3 = new ModelRenderer(64, 20, 32, 0);
        this.shellE3.addBox(0, 0, -4, 4, 4, 4);
        this.shellE4 = new ModelRenderer(64, 20, 48, 0);
        this.shellE4.addBox(0, -4, -4, 4, 4, 4);
        this.shellE5 = new ModelRenderer(64, 20, 0, 8);
        this.shellE5.addBox(-4, -4, 0, 4, 4, 4);
        this.shellE6 = new ModelRenderer(64, 20, 16, 8);
        this.shellE6.addBox(-4, 0, 0, 4, 4, 4);
        this.shellE7 = new ModelRenderer(64, 20, 32, 8);
        this.shellE7.addBox(0, 0, 0, 4, 4, 4);
        this.shellE8 = new ModelRenderer(64, 20, 48, 8);
        this.shellE8.addBox(0, -4, 0, 4, 4, 4);

        this.shellC1 = new ModelRenderer(64,20,0,16);
        this.shellC1.addBox(-4,-1,-1,2,2,2);
        this.shellC2 = new ModelRenderer(64,20,8,16);
        this.shellC2.addBox(2,-1,-1,2,2,2);
        this.shellC3 = new ModelRenderer(64,20,16,16);
        this.shellC3.addBox(-1,-4,-1,2,2,2);
        this.shellC4 = new ModelRenderer(64,20,24,16);
        this.shellC4.addBox(-1,2,-1,2,2,2);
        this.shellC5 = new ModelRenderer(64,20,32,16);
        this.shellC5.addBox(-1,-1,-4,2,2,2);
        this.shellC6 = new ModelRenderer(64,20,40,16);
        this.shellC6.addBox(-1,-1,2,2,2,2);

        this.core = new ModelRenderer(64,20,0,0);
        this.core.addBox(-3.9F, -3.9F, -3.9F, 7.8F, 7.8F, 7.8F);
    }

    public ResourceLocation getTextureLocation(BossRiftEntity rift) {
        return EndPortalTileEntityRenderer.END_PORTAL_LOCATION;
    }

    public void render(BossRiftEntity rift, float v, float v1, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        RANDOM.setSeed(31100L);
        stack.pushPose();
        double multiplier = (double)rift.getPoints() / (double)rift.warpSpan / 20;
        float spinn = rift.time / 2 + rift.revSpeed * 2F + v1;
        stack.pushPose();
        int i = OverlayTexture.NO_OVERLAY;

        stack.translate(0, 0.25D, 0);
        stack.mulPose(Vector3f.YP.rotationDegrees(spinn));

        for(int j = 0; j < 15; ++j) {
            float multi;
            if (j == 0) multi = 0.15F;
            else multi = 2.0F / (float) (18 - j);
            float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * multi;
            float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * multi;
            float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * multi;
            this.core.render(stack, buffer.getBuffer(RENDER_TYPES.get(j)), light, i, f, f1, f2, 1);
        }

        IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);

        stack.translate(-multiplier, -multiplier, -multiplier);
        this.shellE1.render(stack, ivertexbuilder, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellE2.render(stack, ivertexbuilder, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellE3.render(stack, ivertexbuilder, light, i);
        stack.translate(0, multiplier * -2, 0);
        this.shellE4.render(stack, ivertexbuilder, light, i);
        stack.translate(multiplier * -2, 0, multiplier * 2);
        this.shellE5.render(stack, ivertexbuilder, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellE6.render(stack, ivertexbuilder, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellE7.render(stack, ivertexbuilder, light, i);
        stack.translate(0, multiplier * -2, 0);
        this.shellE8.render(stack, ivertexbuilder, light, i);

        stack.translate(-multiplier * 2, multiplier, -multiplier);
        this.shellC1.render(stack, ivertexbuilder, light, i);
        stack.translate(multiplier * 2, 0, 0);
        this.shellC2.render(stack, ivertexbuilder, light, i);
        stack.translate(-multiplier, -multiplier, 0);
        this.shellC3.render(stack, ivertexbuilder, light, i);
        stack.translate(0, multiplier * 2, 0);
        this.shellC4.render(stack, ivertexbuilder, light, i);
        stack.translate(0, -multiplier, -multiplier);
        this.shellC5.render(stack, ivertexbuilder, light, i);
        stack.translate(0, 0, multiplier * 2);
        this.shellC6.render(stack, ivertexbuilder, light, i);

        stack.popPose();
        stack.popPose();

        super.render(rift, v, v1, stack, buffer, light);
    }
}