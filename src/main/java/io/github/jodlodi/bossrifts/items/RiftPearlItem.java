package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPearlItem extends Item implements iRift<ItemStack> {
	public static final ResourceLocation ENTITY_MODEL = BossRifts.prefix("item/rift_pearl_frame");
	public static final ResourceLocation EYE_MODEL = BossRifts.prefix("item/rift_pearl_icon");
	public static final String TOOLTIP_UNLINKED = "tooltip.boss_rifts.rift_pearl.v1_unlinked";
	public static final String TOOLTIP_V1_LINKED = "tooltip.boss_rifts.rift_pearl.v1_linked";
	public static final String TOOLTIP_MODERN_1 = "tooltip.boss_rifts.rift_pearl.modern.1";
	public static final String TOOLTIP_MODERN_2 = "tooltip.boss_rifts.rift_pearl.modern.2";
	public static final String DIMENSION = "AggregatorDimension";
	public static final String POSITION = "AggregatorPosition";
	public static final String POWERED = "RiftPowered";

	public static @Nullable BlockPos respawnPosition = null;
	public static @Nullable RegistryKey<World> respawnDimension = null;

	public RiftPearlItem(Properties properties) {
		super(properties);
	}

	public static boolean isAggregatorPearl(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		return tag != null && (tag.contains(DIMENSION) || tag.contains(POSITION));
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return isAggregatorPearl(stack) ? super.getDescriptionId(stack) + ".aggregator" : super.getDescriptionId(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isAggregatorPearl(stack) || super.isFoil(stack);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (context.isSecondaryUseActive() && !RiftConfig.isV1()) {
			ItemStack stack = context.getItemInHand();
			PlayerEntity player = context.getPlayer();

			int price = -1;
			if (player != null) {
				price = this.getPrice(
						player.blockPosition(),
						stack,
						getRespawnPosition(player)
				);

				if (price <= -1 || (player.experienceLevel < price && !player.abilities.instabuild)) {
					price = -1;
				}
			}

			if (price == -1) {
				long powered = poweredTime(stack);

				if (powered >= 0 && player != null) {
					long diff = player.level.getGameTime() - powered;
					if (diff > RiftConfig.empowerDuration() || diff < TRANSITION_TIME) {
						return ActionResultType.PASS;
					}
				}
			}

			Direction face = context.getClickedFace();
			BlockPos relative = pos.relative(face, 2);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative))
				relative = pos.relative(face, 1);
			if (level.getBlockState(relative).isCollisionShapeFullBlock(level, relative)) return ActionResultType.FAIL;

			BossRiftEntity rift = ModEntities.BOSS_RIFT.get().create(level);
			if (rift != null) {
				rift.moveTo(relative.getX() + 0.5D, relative.getY() + 0.25D, relative.getZ() + 0.5D);
				rift.setItem(context.getItemInHand());
				if (player instanceof ServerPlayerEntity) rift.startRevving((ServerPlayerEntity) player);

				if (player != null && !player.abilities.instabuild) {
					context.getItemInHand().shrink(1);
					if (price > 0) player.giveExperienceLevels(-price);
				}
				return level.addFreshEntity(rift) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
			} else return ActionResultType.FAIL;
		} else if (level.getBlockState(pos).is(ModBlocks.RIFT_AGGREGATOR.get())) {
			level.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			PlayerEntity player = context.getPlayer();
			if (player == null) return super.useOn(context);
			ItemStack stack = context.getItemInHand();

			boolean consume = !player.abilities.instabuild && stack.getCount() == 1;
			if (consume) {
				this.addLodestoneTags(level.dimension(), pos, stack.getOrCreateTag());
			} else {
				ItemStack newStack = new ItemStack(ModItems.RIFT_PEARL.get(), 1);
				CompoundNBT tag = stack.hasTag() ? Objects.requireNonNull(stack.getTag()).copy() : new CompoundNBT();
				newStack.setTag(tag);
				if (!player.abilities.instabuild) stack.shrink(1);

				this.addLodestoneTags(level.dimension(), pos, tag);
				if (!player.inventory.add(newStack)) {
					player.drop(newStack, false);
				}
			}

			return ActionResultType.sidedSuccess(level.isClientSide);
		} else {
			return super.useOn(context);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (RiftConfig.isV1()) return ActionResult.pass(stack);

		int price = this.getPrice(
				player.blockPosition(),
				stack,
				getRespawnPosition(player)
		);

		if (price > -1) {
			if (player.experienceLevel >= price || player.abilities.instabuild) {
				player.startUsingItem(hand);
				return ActionResult.consume(stack);
			} else return ActionResult.fail(stack);
		}

		long powered = poweredTime(stack);

		if (powered >= 0) {
			long diff = player.level.getGameTime() - powered;
			if (diff <= RiftConfig.empowerDuration()) {
				if (diff >= TRANSITION_TIME) {
					player.startUsingItem(hand);
					return ActionResult.consume(stack);
				} else return ActionResult.pass(stack);
			} else {
				diff -= RiftConfig.empowerDuration();
				if (diff <= TRANSITION_TIME) return ActionResult.pass(stack);
			}
		}

		return ActionResult.pass(stack);
	}

	private void addLodestoneTags(RegistryKey<World> key, BlockPos pos, CompoundNBT tag) {
		tag.put(POSITION, NBTUtil.writeBlockPos(pos));
		World.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, key).resultOrPartial(BossRifts.LOGGER::error).ifPresent((inbt) -> tag.put(DIMENSION, inbt));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int remaining) {
		World level = player.level;
		int ticksUsed = USE_DURATION - remaining;
		Vector3d pos = player.getEyePosition(1.0F);

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(getSeed(stack));

		float now = AnimUtil.clampScaled(ticksUsed, USE_DURATION - COMPRESS_OFFSET);
		float before = AnimUtil.scaled(ticksUsed - 1.0F, USE_DURATION - COMPRESS_OFFSET);

		if (plan.movesStarted(now) > plan.movesStarted(before)) {
			level.playLocalSound(pos.x, pos.y, pos.z,
					ModSounds.RIFT_REV_UP.get(), SoundCategory.PLAYERS, 0.25F + 0.5F * now, 0.8F + 0.9F * now, false);
		}

		if (AnimUtil.justFinished(ticksUsed, USE_DURATION - SHUFFLE_OFFSET - 2)) {
			level.playLocalSound(pos.x, pos.y, pos.z,
					ModSounds.RIFT_CLOSE.get(), SoundCategory.PLAYERS, 1.0F, level.random.nextFloat() * 0.5F + 0.8F, false);
		}
	}

	private BlockPos getRespawnPosition(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			return serverPlayer.getRespawnPosition() != null ? serverPlayer.getRespawnPosition() : BlockPos.ZERO;
		} else return respawnPosition != null ? respawnPosition : BlockPos.ZERO;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> list, ITooltipFlag flag) {
		if (!isAggregatorPearl(stack)) list.add(new TranslationTextComponent(TOOLTIP_UNLINKED));
		if (RiftConfig.isV1()) {
			if (isAggregatorPearl(stack)) list.add(new TranslationTextComponent(TOOLTIP_V1_LINKED));
		} else {
			double cost = RiftConfig.getExpCost();
			if (MathHelper.equal(cost, 0.0F)) {
				list.add(new TranslationTextComponent(TOOLTIP_MODERN_1));
				list.add(new TranslationTextComponent(TOOLTIP_MODERN_2));
				return;
			} else {
				Entity entity = stack.getEntityRepresentation();
				if (cost >= 0.0F && entity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) entity;
					int price = this.getPrice(
							player.blockPosition(),
							stack,
							respawnPosition != null ? respawnPosition : BlockPos.ZERO
					);

					if (price > -1) {
						if (player.experienceLevel >= price || player.abilities.instabuild) {
							list.add(new TranslationTextComponent(TOOLTIP_MODERN_1));
							list.add(new TranslationTextComponent(TOOLTIP_MODERN_2));
						}
						list.add(new TranslationTextComponent(BossRiftEntity.COST, new StringTextComponent(String.valueOf(price)).withStyle(TextFormatting.GREEN)));
						return;
					}
				}
			}

			long powered = poweredTime(stack);

			if (powered >= 0 && level != null) {
				long diff = level.getGameTime() - powered;
				if (diff <= RiftConfig.empowerDuration() && diff >= TRANSITION_TIME) {
					list.add(new TranslationTextComponent(TOOLTIP_MODERN_1));
					list.add(new TranslationTextComponent(TOOLTIP_MODERN_2));
				}
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity living) {
		if (living instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) living;
			int price = this.getPrice(
					player.blockPosition(),
					stack,
					getRespawnPosition(player)
			);

			if (price > -1) {
				if (player.experienceLevel >= price || player.abilities.instabuild) {
					if (!player.abilities.instabuild && price > 0) player.giveExperienceLevels(-price);
				} else return stack;
			}
		}

		if (living instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) living;
			Vector3d pos = living.getEyePosition(1.0F);
			level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundCategory.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
			this.sendToSpawn(stack, player.server, player, player);
		}

		Vector3d pos = living.getEyePosition(1.0F);
		level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundCategory.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return USE_DURATION;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean held) {
		stack.setEntityRepresentation(entity);
		long powered = RiftPearlItem.poweredTime(stack);
		if (powered >= 0L) {
			long time = level.getGameTime();
			int soundPoint = TRANSITION_TIME / 2;
			if (time - powered == soundPoint) {
				Vector3d pos = entity.getEyePosition(1.0F);
				entity.level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_OPEN.get(), SoundCategory.PLAYERS, 0.75F, level.random.nextFloat() * 0.4F + 0.4F);
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
		return Optional.of(NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(POSITION)));
	}

	@Override
	public Optional<RegistryKey<World>> savedWorld(ItemStack stack) {
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(DIMENSION)) return Optional.empty();
		return World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, stack.getOrCreateTag().get(DIMENSION)).result();
	}
}
