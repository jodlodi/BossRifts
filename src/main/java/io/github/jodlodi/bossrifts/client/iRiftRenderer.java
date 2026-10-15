package io.github.jodlodi.bossrifts.client;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.jodlodi.bossrifts.BossRifts;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface iRiftRenderer {
	ResourceLocation SHELL_LOCATION = new ResourceLocation(BossRifts.MOD_ID, "textures/entity/boss_rift/boss_rift.png");
	RenderType SHELL_TYPE = RenderType.entityCutoutNoCull(SHELL_LOCATION);
	int SHELL_WIDTH = 64;
	int SHELL_HEIGHT = 32;

	@SuppressWarnings("UnstableApiUsage")
	List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.endPortal(i + 1)).collect(ImmutableList.toImmutableList());
	Random RANDOM = new Random(31100L);

	default ModelRenderer createShell(int u, int v, int x, int y, int z) {
		return new ModelRenderer(SHELL_WIDTH, SHELL_HEIGHT, u * 16, v * 8)
				.addBox(-4 + x * 4, -4 + y * 4, -4 + z * 4, 4, 4, 4);
	}

	default ModelRenderer createPin(int u, int x, int y, int z) {
		return new ModelRenderer(SHELL_WIDTH, SHELL_HEIGHT, u * 8, 16)
				.addBox(-1 + x * 3, -1 + y * 3, -1 + z * 3, 2, 2, 2);
	}

	default float longDelta() {
		return Util.getMillis() / 50.0F;
	}

	float MAX = 360.0F;

	default void spin(MatrixStack ms, float time, float x, float y, float z, float scale) {
		ms.mulPose(Vector3f.XP.rotationDegrees(((time * x) % MAX) * scale));
		ms.mulPose(Vector3f.YP.rotationDegrees(((time * y) % MAX) * scale));
		ms.mulPose(Vector3f.ZP.rotationDegrees(((time * z) % MAX) * scale));
	}
}
