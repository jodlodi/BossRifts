package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPearlItem extends Item implements iRift<ItemStack> {
	public static ResourceLocation ENTITY_MODEL = BossRifts.prefix("item/rift_pearl_frame");
	public static ResourceLocation EYE_MODEL = BossRifts.prefix("item/rift_pearl_icon");
	private final static String DIMENSION = "AggregatorDimension";
	public final static String POSITION = "AggregatorPosition";
	public final static String POWERED = "RiftPowered";

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
		if (level.getBlockState(pos).is(ModBlocks.RIFT_AGGREGATOR.get())) {
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

	private void addLodestoneTags(RegistryKey<World> key, BlockPos pos, CompoundNBT tag) {
		tag.put(POSITION, NBTUtil.writeBlockPos(pos));
		World.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, key).resultOrPartial(BossRifts.LOGGER::error).ifPresent((inbt) -> tag.put(DIMENSION, inbt));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int remaining) {
		World level = player.level;
		if (!level.isClientSide) return;
		int ticksUsed = USE_DURATION - remaining;
		Vector3d pos = player.getEyePosition(Minecraft.getInstance().getFrameTime());

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

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		return ActionResult.consume(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity living) {
		if (living instanceof ServerPlayerEntity) {
			Vector3d pos = living.getEyePosition(1.0F);
			level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundCategory.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
			ServerPlayerEntity player = (ServerPlayerEntity) living;
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
