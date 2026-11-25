package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.EntityHitResult;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class RiftEntityRenderer extends EntityRenderer<BossRiftEntity, BossRiftRenderState> implements iRiftRenderer<BossRiftRenderState> {
	private final ModelPart core;
	private final Map<Vec3i, ModelPart> shell;
	private final ItemModelResolver itemModelResolver;

	public RiftEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.25F;
		this.itemModelResolver = context.getItemModelResolver();

		ModelPart root = context.bakeLayer(BOSS_RIFT_MODEL);
		this.shell = iRiftRenderer.collectShell(root);
		this.core = root.getChild("core");
	}

	@Override
	public BossRiftRenderState createRenderState() {
		return new BossRiftRenderState();
	}

	@Override
	public void extractRenderState(BossRiftEntity rift, BossRiftRenderState state, float partialTick) {
		super.extractRenderState(rift, state, partialTick);

		state.partialTick = partialTick;
		state.seed = rift.getSeed(rift);
		state.time = rift.level().getGameTime();
		state.power = RiftPearlItem.poweredTime(rift.getItem());
		state.points = rift.getPoints();
		state.points0 = rift.getPoints0();
		state.foil = rift.getItem().hasFoil();
	}

	@Override
	protected boolean shouldShowName(BossRiftEntity rift, double distanceToCameraSq) {
		return this.costText(rift) != null || super.shouldShowName(rift, distanceToCameraSq);
	}

	@Override
	protected @Nullable Component getNameTag(BossRiftEntity rift) {
		Component cost = this.costText(rift);
		return cost != null ? cost : super.getNameTag(rift);
	}

	private @Nullable Component costText(BossRiftEntity rift) {
		Minecraft minecraft = Minecraft.getInstance();
		if (Services.CONFIG.isV1() && minecraft.player != null && minecraft.hitResult instanceof EntityHitResult result && rift.equals(result.getEntity())) {
			int price = rift.getPrice(
					rift.blockPosition(),
					rift,
					RiftPearlItem.respawnPosition != null ? RiftPearlItem.respawnPosition : BlockPos.ZERO
			);
			if (price > -1) {
				return Component.translatable(BossRiftEntity.COST, Component.literal(String.valueOf(price)).withStyle(ChatFormatting.GREEN));
			}
		}
		return null;
	}

	@Override
	public void submit(BossRiftRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		this.preRender(state, stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.partialTick);
		super.submit(state, stack, collector, camera);
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
	public long getSeed(BossRiftRenderState state) {
		return state.seed;
	}

	@Override
	public long getTime(BossRiftRenderState state) {
		return state.time;
	}

	@Override
	public long getPower(BossRiftRenderState state) {
		return state.power;
	}

	@Override
	public float getCharge(BossRiftRenderState state) {
		return state.points;
	}

	@Override
	public float getCharge0(BossRiftRenderState state) {
		return state.points0;
	}

	@Override
	public void render(BossRiftRenderState state, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale + 0.25D, 0.0D);

		stack.pushPose();
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		Map<Vec3i, Quaternionf> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(state, shuffleScale);

		int i = 0;
		for (Map.Entry<Vec3i, ModelPart> entry : this.getShell().entrySet()) {
			stack.pushPose();

			if (pose != null) stack.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((exactTime + i++ * 2.5D) * 0.1D) * 0.2D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

			Vec3i vector = entry.getKey();
			stack.translate(vector.getX() * multiplier, vector.getY() * multiplier, vector.getZ() * multiplier);
			collector.submitModelPart(entry.getValue(), stack, SHELL_TYPE, light, overlay, null);
			stack.popPose();
		}

		stack.pushPose();
		stack.scale(portalSize, portalSize, portalSize);
		collector.submitModelPart(this.getCore(), stack, RenderTypes.endPortal(), light, overlay, null);
		stack.popPose();

		stack.popPose();

		stack.pushPose();
		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
		);
		stack.scale(pearlSize * 0.85F, pearlSize * 0.85F, pearlSize * 0.85F);
		Services.CLIENT_HOOKS.renderEye(stack, collector, ItemDisplayContext.FIXED, state.foil, state.outlineColor);
		//state.pearl.submit(stack, collector, 0xF000F0, overlay, state.outlineColor);
		stack.popPose();

		stack.popPose();
	}
}