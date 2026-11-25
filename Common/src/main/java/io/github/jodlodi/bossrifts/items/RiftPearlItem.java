package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.registry.*;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.function.Consumer;

@NullMarked
public class RiftPearlItem extends Item implements iRift<ItemStack> {
	public static final String TOOLTIP_UNLINKED = "tooltip.boss_rifts.rift_pearl.v1_unlinked";
	public static final String TOOLTIP_V1_LINKED = "tooltip.boss_rifts.rift_pearl.v1_linked";
	public static final String TOOLTIP_MODERN_1 = "tooltip.boss_rifts.rift_pearl.modern.1";
	public static final String TOOLTIP_MODERN_2 = "tooltip.boss_rifts.rift_pearl.modern.2";

	public static @Nullable BlockPos respawnPosition = null;
	public static @Nullable ResourceKey<Level> respawnDimension = null;

	public RiftPearlItem(Properties properties) {
		super(properties);
	}

	public static boolean isAggregatorPearl(ItemStack stack) {
		return stack.has(ModDataComponents.AGGREGATOR_TRACKER.get());
	}

	private void makeAggregatorPearl(ResourceKey<Level> key, BlockPos pos, ItemStack stack) {
		stack.set(ModDataComponents.AGGREGATOR_TRACKER.get(), new LodestoneTracker(Optional.of(GlobalPos.of(key, pos)), true));
	}

	public static long poweredTime(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.POWERED_MARKER.get(), -1L);
	}

	public static boolean empowerPearl(ItemStack stack, long time) {
		if (stack.getItem() instanceof RiftPearlItem) {
			stack.set(ModDataComponents.POWERED_MARKER.get(), time);
			return true;
		}
		return false;
	}

	@Override
	public Component getName(ItemStack stack) {
		return isAggregatorPearl(stack) ? Component.translatable("item.bossrifts.rift_pearl.aggregator") : super.getName(stack);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isAggregatorPearl(stack) || super.isFoil(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (context.isSecondaryUseActive() && !Services.CONFIG.isV1()) {
			ItemStack stack = context.getItemInHand();
			Player player = context.getPlayer();

			int price = -1;
			if (player != null) {
				price = this.getPrice(
						player.blockPosition(),
						stack,
						getPlayerRespawnPos(player)
				);

				if (price <= -1 || (player.experienceLevel < price && !player.getAbilities().instabuild)) {
					price = -1;
				}
			}

			if (price == -1) {
				long powered = poweredTime(stack);

				if (powered >= 0 && player != null) {
					long diff = player.level().getGameTime() - powered;
					if (diff > Services.CONFIG.empowerDuration() || diff < TRANSITION_TIME) {
						return InteractionResult.PASS;
					}
				}
			}

			Direction face = context.getClickedFace();
			BlockPos relative = pos.relative(face, 2);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative))
				relative = pos.relative(face, 1);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative)) return InteractionResult.FAIL;

			BossRiftEntity rift = ModEntities.BOSS_RIFT.get().create(level, EntitySpawnReason.TRIGGERED);
			if (rift != null) {
				rift.move(MoverType.SELF, new Vec3(relative.getX() + 0.5D, relative.getY() + 0.25D, relative.getZ() + 0.5D));
				rift.setItem(context.getItemInHand());
				if (player instanceof ServerPlayer serverPlayer) rift.startRevving(serverPlayer);

				if (player != null && !player.getAbilities().instabuild) {
					context.getItemInHand().shrink(1);
					if (price > 0) player.giveExperienceLevels(-price);
				}
				return level.addFreshEntity(rift) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			} else return InteractionResult.FAIL;
		} else if (level.getBlockState(pos).is(ModBlocks.RIFT_AGGREGATOR.get())) {
			level.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
			Player player = context.getPlayer();
			if (player == null) return super.useOn(context);
			ItemStack stack = context.getItemInHand();

			boolean consume = !player.getAbilities().instabuild && stack.getCount() == 1;
			if (consume) {
				this.makeAggregatorPearl(level.dimension(), pos, stack);
			} else {
				ItemStack newStack = stack.copy();
				if (!player.getAbilities().instabuild) stack.shrink(1);

				this.makeAggregatorPearl(level.dimension(), pos, newStack);
				if (!player.addItem(newStack)) player.drop(newStack, false);
			}

			return InteractionResult.SUCCESS;
		} else {
			return super.useOn(context);
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (Services.CONFIG.isV1()) return InteractionResult.PASS;

		ItemStack stack = player.getItemInHand(hand);
		int price = this.getPrice(
				player.blockPosition(),
				stack,
				getPlayerRespawnPos(player)
		);

		if (price > -1) {
			if (player.experienceLevel >= price || player.getAbilities().instabuild) {
				player.startUsingItem(hand);
				return InteractionResult.CONSUME;
			} else return InteractionResult.FAIL;
		}

		long powered = poweredTime(stack);

		if (powered >= 0) {
			long diff = player.level().getGameTime() - powered;
			if (diff <= Services.CONFIG.empowerDuration()) {
				if (diff >= TRANSITION_TIME) {
					player.startUsingItem(hand);
					return InteractionResult.CONSUME;
				} else return InteractionResult.PASS;
			} else {
				diff -= Services.CONFIG.empowerDuration();
				if (diff <= TRANSITION_TIME) return InteractionResult.PASS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onUseTick(Level level, LivingEntity player, ItemStack stack, int remaining) {
		int ticksUsed = USE_DURATION - remaining;
		Vec3 pos = player.getEyePosition(1.0F);

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(getSeed(stack));

		float now = AnimUtil.clampScaled(ticksUsed, USE_DURATION - COMPRESS_OFFSET);
		float before = AnimUtil.scaled(ticksUsed - 1.0F, USE_DURATION - COMPRESS_OFFSET);

		if (plan.movesStarted(now) > plan.movesStarted(before)) {
			level.playLocalSound(pos.x, pos.y, pos.z,
					ModSounds.RIFT_REV_UP.get(), SoundSource.PLAYERS, 0.25F + 0.5F * now, 0.8F + 0.9F * now, false);
		}

		if (AnimUtil.justFinished(ticksUsed, USE_DURATION - SHUFFLE_OFFSET - 2)) {
			level.playLocalSound(pos.x, pos.y, pos.z,
					ModSounds.RIFT_CLOSE.get(), SoundSource.PLAYERS, 1.0F, level.getRandom().nextFloat() * 0.5F + 0.8F, false);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		if (!isAggregatorPearl(stack)) builder.accept(Component.translatable(TOOLTIP_UNLINKED));
		if (Services.CONFIG.isV1()) {
			if (isAggregatorPearl(stack)) builder.accept(Component.translatable(TOOLTIP_V1_LINKED));
		} else {
			double cost = Services.CONFIG.getExpCost();
			if (Mth.equal(cost, 0.0F)) {
				builder.accept(Component.translatable(TOOLTIP_MODERN_1));
				builder.accept(Component.translatable(TOOLTIP_MODERN_2));
				return;
			} else if (cost >= 0.0F && getEntityRepresentation(stack) instanceof Player player) {
				int price = this.getPrice(
						player.blockPosition(),
						stack,
						respawnPosition != null ? respawnPosition : BlockPos.ZERO
				);

				if (price > -1) {
					if (player.experienceLevel >= price || player.getAbilities().instabuild) {
						builder.accept(Component.translatable(TOOLTIP_MODERN_1));
						builder.accept(Component.translatable(TOOLTIP_MODERN_2));
					}
					builder.accept(Component.translatable(BossRiftEntity.COST, Component.literal(String.valueOf(price)).withStyle(ChatFormatting.GREEN)));
					return;
				}
			}

			long powered = poweredTime(stack);

			Entity entity = getEntityRepresentation(stack);
			if (powered >= 0 && entity != null) {
				long diff = entity.level().getGameTime() - powered;
				if (diff <= Services.CONFIG.empowerDuration() && diff >= TRANSITION_TIME) {
					builder.accept(Component.translatable(TOOLTIP_MODERN_1));
					builder.accept(Component.translatable(TOOLTIP_MODERN_2));
				}
			}
		}
	}

	@Nullable
	public static Entity getEntityRepresentation(ItemStack stack) {
		return Minecraft.getInstance().player;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		if (living instanceof Player player) {
			int price = this.getPrice(
					player.blockPosition(),
					stack,
					getPlayerRespawnPos(player)
			);

			if (price > -1) {
				if (player.experienceLevel >= price || player.getAbilities().instabuild) {
					if (!player.getAbilities().instabuild && price > 0) player.giveExperienceLevels(-price);
				} else return stack;
			}
		}

		if (living instanceof ServerPlayer player) {
			Vec3 pos = living.getEyePosition(1.0F);
			level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundSource.PLAYERS, 1F, level.getRandom().nextFloat() * 0.4F + 0.5F);
			this.sendToSpawn(stack, player.level().getServer(), player, player);
		}

		Vec3 pos = living.getEyePosition(1.0F);
		level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundSource.PLAYERS, 1F, level.getRandom().nextFloat() * 0.4F + 0.5F);
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return USE_DURATION;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BLOCK;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		long powered = RiftPearlItem.poweredTime(stack);
		if (powered >= 0L) {
			long time = level.getGameTime();
			int soundPoint = TRANSITION_TIME / 2;
			if (time - powered == soundPoint) {
				Vec3 pos = entity.getEyePosition(1.0F);
				entity.level().playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_OPEN.get(), SoundSource.PLAYERS, 0.75F, level.getRandom().nextFloat() * 0.4F + 0.4F);
			}
		}
	}

	protected BlockPos getPlayerRespawnPos(Player player) {
		return player instanceof ServerPlayer serverPlayer
				? serverPlayer.getRespawnConfig() != null ? serverPlayer.getRespawnConfig().respawnData().pos() : BlockPos.ZERO
				: respawnPosition != null ? respawnPosition : BlockPos.ZERO;
	}

	@Override
	public long getSeed(ItemStack stack) {
		return this.savedPos(stack).map(BlockPos::asLong).orElse(3626523L);
	}

	@Override
	public Optional<BlockPos> savedPos(ItemStack stack) {
		return Optional.ofNullable(stack.get(ModDataComponents.AGGREGATOR_TRACKER.get())).flatMap(LodestoneTracker::target).map(GlobalPos::pos);
	}

	@Override
	public Optional<ResourceKey<Level>> savedWorld(ItemStack stack) {
		return Optional.ofNullable(stack.get(ModDataComponents.AGGREGATOR_TRACKER.get())).flatMap(LodestoneTracker::target).map(GlobalPos::dimension);
	}
}
