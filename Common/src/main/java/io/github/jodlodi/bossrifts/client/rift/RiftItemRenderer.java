package io.github.jodlodi.bossrifts.client.rift;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class RiftItemRenderer implements SpecialModelRenderer<ItemStack>, iRiftRenderer<RiftItemRenderer.RenderInfo> {
	public static final Map<ItemStack, Pair<Long, Boolean>> STACK_MAP = new HashMap<>();

	private final ModelPart core;
	private final Map<Vec3i, ModelPart> shell;
	private final boolean dynamic;
	private final ItemTransform transform;

	protected RiftItemRenderer(ModelPart root, boolean dynamic, ItemTransform transform) {
		this.core = root.getChild("core");
		this.shell = iRiftRenderer.collectShell(root);
		this.dynamic = dynamic;
		this.transform = transform;
	}

	@Override
	public void submit(@Nullable ItemStack argument, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
		if (argument == null || !(argument.getItem() instanceof RiftPearlItem pearl)) return;

		Minecraft minecraft = Minecraft.getInstance();
		float partialTick = minecraft.isPaused() ? 1.0F : minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		this.preRender(new RenderInfo(argument, pearl, this.dynamic, this.transform, findHolder(argument), outlineColor), stack, collector, light, overlay, partialTick);
	}

	@Override
	public @Nullable ItemStack extractArgument(ItemStack stack) {
		return stack;
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					output.accept(new Vector3f(x * 0.75F + 0.125F, y * 0.75F + 0.125F, z * 0.75F + 0.125F));
				}
			}
		}
	}

	private static @Nullable Player findHolder(ItemStack stack) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return null;
		for (Player player : level.players()) {
			if (player.getUseItem() == stack || player.getMainHandItem() == stack || player.getOffhandItem() == stack)
				return player;
		}
		return null;
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
	public long getSeed(RenderInfo info) {
		return info.pearl().getSeed(info.stack());
	}

	@Override
	public long getTime(RenderInfo info) {
		ClientLevel level = Minecraft.getInstance().level;
		return level != null ? level.getGameTime() : -1L;
	}

	@Override
	public long getPower(RenderInfo info) {
		return RiftPearlItem.poweredTime(info.stack());
	}

	@Override
	public float getCharge(RenderInfo info) {
		Player holder = info.holder();
		if (holder != null && holder.isUsingItem() && holder.getUseItem() == info.stack()) {
			return RiftPearlItem.USE_DURATION - holder.getUseItemRemainingTicks();
		}
		return -1.0F;
	}

	@Override
	public float getCharge0(RenderInfo info) {
		return this.getCharge(info) - 1;
	}

	@Override
	public float calculateScale(RenderInfo info, float exactTime) {
		Player player = info.holder();
		if (player != null) {
			int price = info.pearl().getPrice(
					player.blockPosition(),
					info.stack(),
					RiftPearlItem.respawnPosition != null ? RiftPearlItem.respawnPosition : BlockPos.ZERO
			);

			if (price > -1) {
				boolean open = player.experienceLevel >= price || player.getAbilities().instabuild;

				if (!STACK_MAP.containsKey(info.stack())) {
					STACK_MAP.put(info.stack(), Pair.of(player.level().getGameTime(), !open));
				} else if (open == STACK_MAP.get(info.stack()).getSecond()) {
					STACK_MAP.put(info.stack(), Pair.of(player.level().getGameTime(), !open));
				}

				float scale = 1.0F;
				long powered = STACK_MAP.get(info.stack()).getFirst();
				float diff = exactTime - powered;

				if (diff > 0) {
					if (open) scale = AnimUtil.clamp(1.0F - (diff / RiftPearlItem.TRANSITION_TIME));
					else scale = AnimUtil.clamp(diff / RiftPearlItem.TRANSITION_TIME);
				}

				return scale;
			}
		}
		return iRiftRenderer.super.calculateScale(info, exactTime);
	}

	@Override
	public void render(RenderInfo info, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, float exactTime, float scale, float antiScale, float close, float pearlSize, float portalSize, float shuffleScale, float useScale, float spinBonus) {
		stack.pushPose();
		if (info.dynamic()) stack.translate(0.0D, Math.cos(exactTime * 0.125D) * -0.025F * scale, 0.0D);

		stack.pushPose();
		this.transform.apply(false, stack.last());
		stack.scale(1.5F, 1.5F, 1.5F);

		this.spin(stack,
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.125F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.375F),
				Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * 0.625F)
		);

		Map<Vec3i, Quaternionf> pose = null;
		if (shuffleScale >= 0.0F) pose = this.computePose(info, shuffleScale);

		int i = 0;
		for (Map.Entry<Vec3i, ModelPart> entry : this.getShell().entrySet()) {
			stack.pushPose();

			if (pose != null) stack.mulPose(pose.get(entry.getKey()));

			double multiplier = ((Math.sin((exactTime + i++ * 2.5D) * 0.1D) * 0.2D + 0.5D) * antiScale + 0.5D * scale) * 0.125D * close * useScale;

			Vec3i vector = entry.getKey();
			stack.translate(vector.getX() * multiplier, vector.getY() * multiplier, vector.getZ() * multiplier);
			collector.submitModelPart(entry.getValue(), stack, SHELL_TYPE, light, OverlayTexture.NO_OVERLAY, null);
			stack.popPose();
		}

		stack.pushPose();
		stack.scale(portalSize, portalSize, portalSize);
		collector.submitModelPart(this.getCore(), stack, RenderTypes.endPortal(), light, OverlayTexture.NO_OVERLAY, null);
		stack.popPose();

		stack.popPose();

		stack.pushPose();
		if (info.dynamic()) {
			this.spin(stack,
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.375F),
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.625F),
					Mth.wrapDegrees((exactTime % 2880.0F + spinBonus) * -0.125F)
			);
		} else pearlSize *= 0.75F;

		stack.scale(pearlSize, pearlSize, pearlSize);

		Services.CLIENT_HOOKS.renderShields(stack, collector);
		stack.popPose();
		stack.popPose();
	}

	public record RenderInfo(ItemStack stack, RiftPearlItem pearl, boolean dynamic, ItemTransform transform,
							 @Nullable Player holder,
							 int outlineColor) {
	}

	public record Unbaked(ItemDisplayContext context, ItemTransform transform) implements SpecialModelRenderer.Unbaked<ItemStack> {
		public static final Identifier ID = Constants.prefix("rift");

		public static final MapCodec<Vector3fc> VECTOR_3_F_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.FLOAT.fieldOf("x").forGetter(Vector3fc::x),
				Codec.FLOAT.fieldOf("y").forGetter(Vector3fc::y),
				Codec.FLOAT.fieldOf("z").forGetter(Vector3fc::z)
		).apply(instance, Vector3f::new));

		public static final MapCodec<ItemTransform> ITEM_TRANSFORM_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				VECTOR_3_F_CODEC.fieldOf("rotation").forGetter(ItemTransform::rotation),
				VECTOR_3_F_CODEC.fieldOf("translation").forGetter(ItemTransform::translation),
				VECTOR_3_F_CODEC.fieldOf("scale").forGetter(ItemTransform::scale)
		).apply(instance, ItemTransform::new));

		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemDisplayContext.CODEC.fieldOf("context").forGetter(Unbaked::context),
				ITEM_TRANSFORM_CODEC.fieldOf("transform").forGetter(Unbaked::transform)
		).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
			return new RiftItemRenderer(
					context.entityModelSet().bakeLayer(BOSS_RIFT_MODEL),
					this.context != ItemDisplayContext.GUI && this.context != ItemDisplayContext.FIXED,
					this.transform
			);
		}
	}
}