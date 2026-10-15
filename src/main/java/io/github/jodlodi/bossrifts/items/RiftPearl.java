package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPearl extends Item {
	public static ResourceLocation ENTITY_MODEL = BossRifts.prefix("item/rift_pearl_frame");
	public static ResourceLocation EYE_MODEL = BossRifts.prefix("item/rift_pearl_icon");
	private final static String DIMENSION = "AggregatorDimension";
	private final static String POSITION = "AggregatorPosition";
	public final static String POWERED = "RiftPowered";

	public final static int EMPOWER_DURATION = 300;

	public RiftPearl(Properties properties) {
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
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		player.startUsingItem(hand);
		if (level instanceof ServerWorld) {
			ServerWorld serverLevel = (ServerWorld) level;

			BossRiftEntity rift = new BossRiftEntity(serverLevel);
			rift.setPos(player.getX(), player.getEyeY(), player.getZ());
			serverLevel.addFreshEntity(rift);
			if (stack.getOrCreateTag().contains(POSITION)) {
				rift.setDestination(NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(POSITION)));
			}

			if (!player.abilities.instabuild) stack.shrink(1);

			player.awardStat(Stats.ITEM_USED.get(this));
			player.swing(hand, true);
			return ActionResult.success(stack);
		}

		return ActionResult.consume(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean held) {
		stack.setEntityRepresentation(entity);
	}
}
