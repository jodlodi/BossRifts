package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.AnimUtil;
import io.github.jodlodi.bossrifts.RiftCubeSolver;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPearl extends Item {
	public static ResourceLocation ENTITY_MODEL = BossRifts.prefix("item/rift_pearl_frame");
	public static ResourceLocation EYE_MODEL = BossRifts.prefix("item/rift_pearl_icon");
	private final static String DIMENSION = "AggregatorDimension";
	public final static String POSITION = "AggregatorPosition";
	public final static String POWERED = "RiftPowered";

	public final static int USE_DURATION = 300;
	public final static int EMPOWER_DURATION = 500;
	public final static int TRANSITION_TIME = 50;
	public final static int SHUFFLE_OFFSET = 5;
	public final static int COMPRESS_OFFSET = 10;

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
	public void onUsingTick(ItemStack stack, LivingEntity player, int remaining) {
		World level = player.level;
		if (!level.isClientSide) return;
		int ticksUsed = USE_DURATION - remaining;
		Vector3d pos = player.getEyePosition(Minecraft.getInstance().getFrameTime());

		long seed = 3626523L;
		if (stack.getOrCreateTag().contains(RiftPearl.POSITION)) {
			seed = NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(RiftPearl.POSITION)).asLong();
		}

		RiftCubeSolver.Plan plan = RiftCubeSolver.plan(seed);

		float now = AnimUtil.clampScaled(ticksUsed , USE_DURATION - COMPRESS_OFFSET);
		float before =  AnimUtil.scaled(ticksUsed - 1.0F, USE_DURATION - COMPRESS_OFFSET);

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
			this.sendToSpawn(player.server, player, player, stack);
		}
		Vector3d pos = living.getEyePosition(1.0F);
		level.playSound(null, pos.x, pos.y, pos.z, ModSounds.RIFT_WARP.get(), SoundCategory.PLAYERS, 1F, level.random.nextFloat() * 0.4F + 0.5F);
		return stack;
	}

	public Vector3d getSpawnPos(MinecraftServer server, ServerPlayerEntity serverPlayer, ItemStack stack) {
		BlockPos spawnPoint = serverPlayer.getRespawnPosition();
		float viewAngle = serverPlayer.getRespawnAngle();
		ServerWorld spawnWorld = server.getLevel(serverPlayer.getRespawnDimension());
		RegistryKey<World> itemWorld = getLodestoneDimension(stack);
		if (itemWorld != null) spawnWorld = server.getLevel(itemWorld);

		if (stack.hasTag() && stack.getOrCreateTag().contains(POSITION)) {
			spawnPoint = NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(POSITION));
		}

		if (spawnPoint != null && spawnWorld != null) {
			Optional<Vector3d> vec3 = PlayerEntity.findRespawnPositionAndUseSpawnBlock(spawnWorld, spawnPoint, viewAngle, false, true);
			if (vec3.isPresent()) return vec3.get();
		}

		spawnWorld = Objects.requireNonNull(server.getLevel(World.OVERWORLD));
		return new Vector3d(spawnWorld.getSharedSpawnPos().getX() + 0.5D, spawnWorld.getSharedSpawnPos().getY(), spawnWorld.getSharedSpawnPos().getZ() + 0.5D);
	}

	public void sendToSpawn(MinecraftServer server, Entity entity, ServerPlayerEntity serverPlayer, ItemStack stack) {
		ServerWorld spawnWorld = server.getLevel(serverPlayer.getRespawnDimension());
		RegistryKey<World> itemWorld = getLodestoneDimension(stack);
		if (itemWorld != null) spawnWorld = server.getLevel(itemWorld);
		if (spawnWorld == null) spawnWorld = Objects.requireNonNull(server.getLevel(World.OVERWORLD));

		Vector3d vector3d = this.getSpawnPos(server, serverPlayer, stack);

		EntityTeleportEvent.TeleportCommand event = ForgeEventFactory.onEntityTeleportCommand(entity, vector3d.x, vector3d.y, vector3d.z);
		if (event.isCanceled()) return;

		double dx = event.getTargetX();
		double dy = event.getTargetY();
		double dz = event.getTargetZ();

		if (entity instanceof ServerPlayerEntity) {
			ChunkPos chunkpos = new ChunkPos(new BlockPos(dx, dy, dz));
			spawnWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());
			entity.stopRiding();

			if (((ServerPlayerEntity) entity).isSleeping()) ((ServerPlayerEntity) entity).stopSleepInBed(true, true);
			if (spawnWorld == entity.level) {
				((ServerPlayerEntity) entity).connection.teleport(dx, dy, dz, entity.yRot, entity.xRot);
			} else {
				((ServerPlayerEntity) entity).teleportTo(spawnWorld, dx, dy, dz, entity.yRot, entity.xRot);
			}
		} else {
			float f1 = MathHelper.wrapDegrees(entity.yRot);
			float f = MathHelper.wrapDegrees(entity.xRot);

			f = MathHelper.clamp(f, -90.0F, 90.0F);
			if (spawnWorld == entity.level) {
				entity.moveTo(dx, dy, dz, f1, f);
				entity.setYHeadRot(f1);
			} else {
				entity.unRide();
				Entity newEntity = entity.getType().create(spawnWorld);
				if (newEntity != null) {
					newEntity.restoreFrom(entity);
					entity.remove();
					newEntity.moveTo(dx, dy, dz, f1, f);
					newEntity.setYHeadRot(f1);
					spawnWorld.addFreshEntity(newEntity);
				}
			}
		}
		if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}
		if (entity instanceof CreatureEntity) ((CreatureEntity) entity).getNavigation().stop();
		if (entity instanceof ItemEntity) ((ItemEntity) entity).setExtendedLifetime();
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
		long powered = RiftPearl.poweredTime(stack);
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

	@Nullable
	public static RegistryKey<World> getLodestoneDimension(ItemStack stack) {
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(DIMENSION)) return null;
		return World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, stack.getOrCreateTag().get(DIMENSION)).result().orElse(null);
	}
}
