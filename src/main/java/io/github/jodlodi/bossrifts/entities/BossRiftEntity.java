package io.github.jodlodi.bossrifts.entities;

import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BossRiftEntity extends Entity implements iRift<BossRiftEntity> {
	private static final DataParameter<Integer> DATA_WARP_POINTS = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.INT);
	private static final DataParameter<ItemStack> DATA_ITEM = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.ITEM_STACK);
	protected static final DataParameter<Optional<UUID>> DATA_OWNERUUID_ID = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.OPTIONAL_UUID);
	private static final double RANGE = 2.5D;

	private int clientWarp = 0;
	private int clientWarp0 = 0;

	private boolean revving = false;

	public BossRiftEntity(EntityType<? extends BossRiftEntity> entityType, World world) {
		super(entityType, world);
		this.blocksBuilding = true;
	}

	public BossRiftEntity(FMLPlayMessages.SpawnEntity ignoredSpawnEntity, World world) {
		this(world);
	}

	public BossRiftEntity(World world) {
		super(ModEntities.BOSS_RIFT.get(), world);
		this.blocksBuilding = true;
	}

	public boolean isRevving() {
		return this.revving;
	}

	public void stopRevving() {
		if (RiftConfig.isV1()) this.revving = false;
	}

	public void startRevving(ServerPlayerEntity player) {
		this.revving = true;
		this.entityData.set(DATA_OWNERUUID_ID, Optional.of(player.getUUID()));
	}

	@Nullable
	public UUID getLastToTouch() {
		return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_WARP_POINTS, 0);
		this.entityData.define(DATA_ITEM, new ItemStack(ModItems.RIFT_PEARL.get()));
		this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
	}

	public ItemStack getItem() {
		ItemStack item = this.entityData.get(DATA_ITEM);
		return Optional.ofNullable(item.isEmpty() ? null : item).orElse(new ItemStack(ModItems.RIFT_PEARL.get()));
	}

	public void setItem(ItemStack stack) {
		this.entityData.set(DATA_ITEM, stack);
	}

	public int addPoints(int add) {
		int newPoints = this.entityData.get(DATA_WARP_POINTS) + add;
		this.entityData.set(DATA_WARP_POINTS, newPoints);
		return newPoints;
	}

	public void setPoints(int points) {
		this.clientWarp = points;
	}

	public int getPoints() {
		return this.clientWarp;
	}

	public void setPoints0(int points) {
		this.clientWarp0 = points;
	}

	public int getPoints0() {
		return this.clientWarp0;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return this.getItem();
	}

	@Override
	public void tick() {
		int points = this.entityData.get(DATA_WARP_POINTS);

		double thisX = this.getX();
		double thisY = this.getY();
		double thisZ = this.getZ();

		if (this.level.isClientSide) {
			this.setPoints0(this.getPoints());
			this.setPoints(this.entityData.get(DATA_WARP_POINTS));

			RiftCubeSolver.Plan plan = RiftCubeSolver.plan(this.getSeed(this));

			float now = AnimUtil.clampScaled(this.getPoints(), RiftPearlItem.USE_DURATION - RiftPearlItem.COMPRESS_OFFSET);
			float before = AnimUtil.scaled(this.getPoints0(), RiftPearlItem.USE_DURATION - RiftPearlItem.COMPRESS_OFFSET);

			if (plan.movesStarted(now) != plan.movesStarted(before)) {
				this.level.playLocalSound(thisX, thisY, thisZ,
						ModSounds.RIFT_REV_UP.get(), SoundCategory.PLAYERS, 0.25F + 0.5F * now, 0.8F + 0.9F * now, false);
			}

			if (AnimUtil.justFinished(this.getPoints(), RiftPearlItem.USE_DURATION - RiftPearlItem.SHUFFLE_OFFSET - 2)) {
				this.level.playLocalSound(thisX, thisY, thisZ,
						ModSounds.RIFT_CLOSE.get(), SoundCategory.PLAYERS, 1.0F, this.level.random.nextFloat() * 0.5F + 0.8F, false);
			}

			double maxX = this.getRandomX(0.1D) + RANGE;
			double maxY = this.getRandomY(0.1D) + RANGE + 0.25D;
			double maxZ = this.getRandomZ(0.1D) + RANGE;
			double minX = this.getRandomX(0.1D) - RANGE;
			double minY = this.getRandomY(0.1D) - RANGE - 0.25D;
			double minZ = this.getRandomZ(0.1D) - RANGE;

			addRandomStationaryParticle(AnimUtil.random(this.random, -RANGE, RANGE) + thisX, maxY, maxZ);
			addRandomStationaryParticle(AnimUtil.random(this.random, -RANGE, RANGE) + thisX, maxY, minZ);
			addRandomStationaryParticle(AnimUtil.random(this.random, -RANGE, RANGE) + thisX, minY, maxZ);
			addRandomStationaryParticle(AnimUtil.random(this.random, -RANGE, RANGE) + thisX, minY, minZ);

			addRandomStationaryParticle(maxX, AnimUtil.random(this.random, -RANGE, RANGE) + thisY, maxZ);
			addRandomStationaryParticle(maxX, AnimUtil.random(this.random, -RANGE, RANGE) + thisY, minZ);
			addRandomStationaryParticle(minX, AnimUtil.random(this.random, -RANGE, RANGE) + thisY, maxZ);
			addRandomStationaryParticle(minX, AnimUtil.random(this.random, -RANGE, RANGE) + thisY, minZ);

			addRandomStationaryParticle(maxX, maxY, AnimUtil.random(this.random, -RANGE, RANGE) + thisZ);
			addRandomStationaryParticle(maxX, minY, AnimUtil.random(this.random, -RANGE, RANGE) + thisZ);
			addRandomStationaryParticle(minX, maxY, AnimUtil.random(this.random, -RANGE, RANGE) + thisZ);
			addRandomStationaryParticle(minX, minY, AnimUtil.random(this.random, -RANGE, RANGE) + thisZ);

			addRandomStationaryParticle(thisX - 0.5D + this.random.nextDouble(), thisY + 0.25D, thisZ - 0.5D + this.random.nextDouble());

			List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(RANGE - 0.25D).move(0, 0.2D, 0), EntityPredicates.ENTITY_STILL_ALIVE);
			for (Entity entity : nearbyEntities) {
				if (!(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity) && this.random.nextInt(USE_DURATION) <= points + USE_DURATION / 3) {
					double itemFix = 0D;
					if (entity instanceof ItemEntity) itemFix = 0.25D;
					this.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D - itemFix), entity.getRandomY() + itemFix, entity.getRandomZ(0.5D - itemFix), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix);
				}
			}
		} else {
			if (this.isRevving()) {
				if (points >= USE_DURATION) {
					MinecraftServer server = this.getServer();
					if (server != null) {
						UUID lastTo = this.getLastToTouch();
						ServerPlayerEntity player = lastTo != null ? server.getPlayerList().getPlayer(lastTo) : null;
						boolean worked = false;

						List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(RANGE - 0.25D).move(0, 0.2D, 0), EntityPredicates.ENTITY_STILL_ALIVE);
						for (Entity entity : nearbyEntities) {
							if (!entity.getUUID().equals(lastTo) && !(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity)) {
								this.sendToSpawn(this, server, entity, lastTo);
								worked = true;
							}
						}

						if (RiftConfig.isOneTimeUse()) this.kill();
						else {
							if (RiftConfig.isV1()) this.stopRevving();
							else {
								ItemEntity item = this.spawnAtLocation(this.getItem());
								if (item != null) {
									server.tell(new TickDelayedTask(server.getTickCount(), () -> this.sendToSpawn(this, server, item, lastTo)));
									worked = true;
								}
								this.kill();
							}
						}

						if (player != null) {
							if (nearbyEntities.contains(player)) {
								server.tell(new TickDelayedTask(server.getTickCount(), () -> this.sendToSpawn(this, server, player, player)));
								worked = true;
							}
							server.tell(new TickDelayedTask(server.getTickCount(), () -> validateSpawn(server, player, nearbyEntities.isEmpty())));
						}

						this.entityData.set(DATA_WARP_POINTS, 0);
						if (worked) {
							this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundCategory.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
						}
					}
				} else this.addPoints(1);
			} else if (points > 0) {
				if (this.addPoints(-Math.min(points, 2)) == 0) {
					this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_CLOSE.get(), SoundCategory.BLOCKS, 0.3F, this.random.nextFloat() * 0.4F + 0.4F);
				}
			} else {
				if (RiftConfig.isExpired(this.level.getGameTime() - RiftPearlItem.poweredTime(this.getItem()) - (RiftPearlItem.TRANSITION_TIME / 2))) {
					if (RiftConfig.isV1()) {
						this.kill();
					} else {
						this.spawnAtLocation(this.getItem());
						this.kill();
					}
				}
			}
		}
	}

	public double getRandomY(double size) {
		return this.getY((2.0D * this.random.nextDouble() - 1.0D) * size);
	}

	private void addRandomStationaryParticle(double x, double y, double z) {
		BasicParticleType particleType = ParticleTypes.SMOKE;
		if (this.random.nextInt(USE_DURATION) < this.entityData.get(DATA_WARP_POINTS))
			this.level.addParticle(particleType, x, y, z, 0, 0, 0);
	}

	@Override
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		if (!this.isRevving() && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			this.startRevving(serverPlayer);
			this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_OPEN.get(), SoundCategory.BLOCKS, 0.8F, this.random.nextFloat() * 0.4F + 0.4F);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	public void validateSpawn(MinecraftServer server, ServerPlayerEntity serverPlayer, boolean check) {
		BlockPos spawnPoint = serverPlayer.getRespawnPosition();
		float viewAngle = serverPlayer.getRespawnAngle();
		ServerWorld serverworld = server.getLevel(serverPlayer.getRespawnDimension());

		if (serverworld != null && spawnPoint != null) {
			PlayerEntity.findRespawnPositionAndUseSpawnBlock(serverworld, spawnPoint, viewAngle, false, check);
			if (serverworld.getBlockState(spawnPoint).is(Blocks.RESPAWN_ANCHOR) && !check) {
				server.tell(new TickDelayedTask(server.getTickCount(), () ->
						serverPlayer.connection.send(new SPlaySoundEffectPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), 1.0F, 1.0F))));
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT tag) {
		tag.putInt("tick_count", this.tickCount);
		tag.putInt("warp_points", this.getEntityData().get(DATA_WARP_POINTS));
		tag.put("item", this.getItem().save(new CompoundNBT()));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT tag) {
		this.tickCount = tag.getInt("tick_count");
		this.getEntityData().set(DATA_WARP_POINTS, tag.getInt("warp_points"));
		this.setItem(ItemStack.of(tag.getCompound("Item")));
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.getEntity() instanceof PlayerEntity && !source.isProjectile() && !source.isMagic()) {
			this.stopRevving();
			this.markHurt();
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public void onAddedToWorld() {
		this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_SPAWN.get(), SoundCategory.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.6F);
		super.onAddedToWorld();
	}

	@Override
	public void kill() {
		this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_EXPIRE.get(), SoundCategory.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.4F);
		super.kill();
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public long getSeed(BossRiftEntity rift) {
		return this.savedPos(rift).map(BlockPos::asLong).orElse(3626523L);
	}

	@Override
	public Optional<BlockPos> savedPos(BossRiftEntity rift) {
		ItemStack stack = rift.getItem();
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(RiftPearlItem.POSITION)) return Optional.empty();
		return Optional.of(NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(RiftPearlItem.POSITION)));
	}

	@Override
	public Optional<RegistryKey<World>> savedWorld(BossRiftEntity rift) {
		ItemStack stack = rift.getItem();
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(RiftPearlItem.DIMENSION)) return Optional.empty();
		return World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, stack.getOrCreateTag().get(RiftPearlItem.DIMENSION)).result();
	}
}