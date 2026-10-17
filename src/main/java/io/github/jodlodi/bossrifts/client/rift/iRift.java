package io.github.jodlodi.bossrifts.client.rift;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.jodlodi.bossrifts.BossRifts;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface iRift {
	ResourceLocation SHELL_LOCATION = new ResourceLocation(BossRifts.MOD_ID, "textures/entity/boss_rift/boss_rift.png");
	RenderType SHELL_TYPE = RenderType.entityCutoutNoCull(SHELL_LOCATION);
	int SHELL_WIDTH = 64;
	int SHELL_HEIGHT = 32;

	@SuppressWarnings("UnstableApiUsage")
	List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.endPortal(i + 1)).collect(ImmutableList.toImmutableList());
	Random RANDOM = new Random(31100L);

	default ModelRenderer createCore() {
		return new ModelRenderer(64, 32, 0, 0)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
	}

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

	default void spin(MatrixStack ms, float x, float y, float z) {
		ms.mulPose(Vector3f.XP.rotationDegrees(x));
		ms.mulPose(Vector3f.YP.rotationDegrees(y));
		ms.mulPose(Vector3f.ZP.rotationDegrees(z));
	}
}
