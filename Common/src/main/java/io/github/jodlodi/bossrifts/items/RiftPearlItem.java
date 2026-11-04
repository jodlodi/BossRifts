package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@NullMarked
public class RiftPearlItem extends Item implements iRift<ItemStack> {
	public static final ResourceLocation ENTITY_MODEL = Constants.prefix("item/rift_pearl_frame");
	public static final ResourceLocation EYE_MODEL = Constants.prefix("item/rift_pearl_icon");
	public static final String TOOLTIP_UNLINKED = "tooltip.boss_rifts.rift_pearl.v1_unlinked";
	public static final String TOOLTIP_V1_LINKED = "tooltip.boss_rifts.rift_pearl.v1_linked";
	public static final String TOOLTIP_MODERN_1 = "tooltip.boss_rifts.rift_pearl.modern.1";
	public static final String TOOLTIP_MODERN_2 = "tooltip.boss_rifts.rift_pearl.modern.2";
	public static final String DIMENSION = "AggregatorDimension";
	public static final String POSITION = "AggregatorPosition";
	public static final String POWERED = "RiftPowered";

	public static @Nullable BlockPos respawnPosition = null;
	public static @Nullable ResourceKey<Level> respawnDimension = null;

	public RiftPearlItem(Properties properties) {
		super(properties);
	}

	public static boolean isAggregatorPearl(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && (tag.contains(DIMENSION) || tag.contains(POSITION));
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return isAggregatorPearl(stack) ? super.getDescriptionId(stack) + ".aggregator" : super.getDescriptionId(stack);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isAggregatorPearl(stack) || super.isFoil(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
			serverPlayer.displayClientMessage(new TextComponent("WA " + Services.PLATFORM.getPlatformName()), true);
		}

		if (context.isSecondaryUseActive() && !Services.CONFIG.isV1()) {
			ItemStack stack = context.getItemInHand();
			Player player = context.getPlayer();
			long powered = poweredTime(stack);

			if (powered >= 0 && player != null) {
				long diff = player.level.getGameTime() - powered;
				if (diff > Services.CONFIG.empowerDuration() || diff < TRANSITION_TIME) {
					return InteractionResult.PASS;
				}
			}

			Direction face = context.getClickedFace();
			BlockPos relative = pos.relative(face, 2);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative))
				relative = pos.relative(face, 1);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative)) return InteractionResult.FAIL;

			BossRiftEntity rift = ModEntities.BOSS_RIFT.get().create(level);
			if (rift != null) {
				rift.moveTo(relative.getX() + 0.5D, relative.getY() + 0.25D, relative.getZ() + 0.5D);
				rift.setItem(context.getItemInHand());
				if (player instanceof ServerPlayer serverPlayer) rift.startRevving(serverPlayer);

				if (player != null && !player.getAbilities().instabuild) context.getItemInHand().shrink(1);
				return level.addFreshEntity(rift) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			} else return InteractionResult.FAIL;
		} else if (level.getBlockState(pos).is(ModBlocks.RIFT_AGGREGATOR.get())) {
			level.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
			Player player = context.getPlayer();
			if (player == null) return super.useOn(context);
			ItemStack stack = context.getItemInHand();

			boolean consume = !player.getAbilities().instabuild && stack.getCount() == 1;
			if (consume) {
				this.addLodestoneTags(level.dimension(), pos, stack.getOrCreateTag());
			} else {
				ItemStack newStack = new ItemStack(ModItems.RIFT_PEARL.get(), 1);
				CompoundTag tag = stack.hasTag() ? Objects.requireNonNull(stack.getTag()).copy() : new CompoundTag();
				newStack.setTag(tag);
				if (!player.getAbilities().instabuild) stack.shrink(1);

				this.addLodestoneTags(level.dimension(), pos, tag);
				if (!player.addItem(newStack)) player.drop(newStack, false);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		} else {
			return super.useOn(context);
		}
	}

	private void addLodestoneTags(ResourceKey<Level> key, BlockPos pos, CompoundTag tag) {
		tag.put(POSITION, NbtUtils.writeBlockPos(pos));
		Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, key).resultOrPartial(Constants.LOG::error).ifPresent((inbt) -> tag.put(DIMENSION, inbt));
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
					ModSounds.RIFT_CLOSE.get(), SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.5F + 0.8F, false);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (Services.CONFIG.isV1()) return InteractionResultHolder.pass(stack);
		long powered = poweredTime(stack);

		if (powered >= 0) {
			long diff = player.level.getGameTime() - powered;
			if (diff <= Services.CONFIG.empowerDuration()) {
				if (diff >= TRANSITION_TIME) {
					player.startUsingItem(hand);
					return InteractionResultHolder.consume(stack);
				} else return InteractionResultHolder.pass(stack);
			} else {
				diff -= Services.CONFIG.empowerDuration();
				if (diff <= TRANSITION_TIME) return InteractionResultHolder.pass(stack);
			}
		}

		if (player instanceof ServerPlayer serverPlayer) {
			int price = this.getPrice(
					serverPlayer.blockPosition(),
					stack,
					serverPlayer.getRespawnPosition() != null ? serverPlayer.getRespawnPosition() : BlockPos.ZERO
			);

			if (price > -1 && (player.totalExperience >= price || player.getAbilities().instabuild)) {
				if (!player.getAbilities().instabuild && price > 0) player.giveExperienceLevels(-price);
				empowerPearl(stack, level.getGameTime());
				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (!isAggregatorPearl(stack)) list.add(new TranslatableComponent(TOOLTIP_UNLINKED));
		if (Services.CONFIG.isV1()) {
			if (isAggregatorPearl(stack)) list.add(new TranslatableComponent(TOOLTIP_V1_LINKED));
		} else {
			long powered = poweredTime(stack);

			if (powered >= 0 && level != null) {
				long diff = level.getGameTime() - powered;
				if (diff <= Services.CONFIG.empowerDuration() && diff >= TRANSITION_TIME) {
					list.add(new TranslatableComponent(TOOLTIP_MODERN_1));
					list.add(new TranslatableComponent(TOOLTIP_MODERN_2));
				}
			}

			if (stack.getEntityRepresentation() != null) {
				int price = this.getPrice(
						stack.getEntityRepresentation().blockPosition(),
						stack,
						respawnPosition != null ? respawnPosition : BlockPos.ZERO
				);
				if (price > 0) {
					list.add(new TranslatableComponent(BossRiftEntity.COST, new TextComponent(String.valueOf(price)).withStyle(ChatFormatting.GREEN)));
				}
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		if (living instanceof ServerPlayer player) {
			Vec3 pos = living.getEyePosition(1.0F);
			level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundSource.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
			this.sendToSpawn(stack, player.server, player, player);
		}
		Vec3 pos = living.getEyePosition(1.0F);
		level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundSource.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return USE_DURATION;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
		stack.setEntityRepresentation(entity);
		long powered = RiftPearlItem.poweredTime(stack);
		if (powered >= 0L) {
			long time = level.getGameTime();
			int soundPoint = TRANSITION_TIME / 2;
			if (time - powered == soundPoint) {
				Vec3 pos = entity.getEyePosition(1.0F);
				entity.level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_OPEN.get(), SoundSource.PLAYERS, 0.75F, level.random.nextFloat() * 0.4F + 0.4F);
			}
		}
	}

	public static long poweredTime(ItemStack stack) {
		return stack.hasTag() && stack.getOrCreateTag().contains(POWERED) ? stack.getOrCreateTag().getLong(POWERED) : -1L;
	}

	public static boolean empowerPearl(ItemStack stack, long time) {
		if (stack.getItem() instanceof RiftPearlItem) {
			stack.getOrCreateTag().putLong(POWERED, time);
			return true;
		}
		return false;
	}

	@Override
	public long getSeed(ItemStack stack) {
		return this.savedPos(stack).map(BlockPos::asLong).orElse(3626523L);
	}

	@Override
	public Optional<BlockPos> savedPos(ItemStack stack) {
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(POSITION)) return Optional.empty();
		return Optional.of(NbtUtils.readBlockPos(stack.getOrCreateTag().getCompound(POSITION)));
	}

	@Override
	public Optional<ResourceKey<Level>> savedWorld(ItemStack stack) {
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(DIMENSION)) return Optional.empty();
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get(DIMENSION)).result();
	}
}
