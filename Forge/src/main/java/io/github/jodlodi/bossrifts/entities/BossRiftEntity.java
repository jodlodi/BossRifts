package io.github.jodlodi.bossrifts.entities;

import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.RiftCubeSolver;
import io.github.jodlodi.bossrifts.util.iRift;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class BossRiftEntity extends Entity implements iRift<BossRiftEntity> {
	protected static final EntityDataAccessor<Integer> DATA_WARP_POINTS = SynchedEntityData.defineId(BossRiftEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(BossRiftEntity.class, EntityDataSerializers.ITEM_STACK);
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(BossRiftEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	public static final String COST = "msg.boss_rifts.exp.cost";

	private int clientWarp = 0;
	private int clientWarp0 = 0;

	private boolean revving = false;

	public BossRiftEntity(EntityType<? extends BossRiftEntity> entityType, Level world) {
		super(entityType, world);
		this.blocksBuilding = true;
	}

	public BossRiftEntity(FMLPlayMessages.SpawnEntity spawnEntity, Level level) {
		this(level);
	}

	public BossRiftEntity(Level world) {
		super(ModEntities.BOSS_RIFT.get(), world);
		this.blocksBuilding = true;
	}

	public boolean isRevving() {
		return this.revving;
	}

	public void stopRevving() {
		if (RiftConfig.isV1()) this.revving = false;
	}

	public void startRevving(ServerPlayer player) {
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
	public ItemStack getPickedResult(HitResult target) {
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
						ModSounds.RIFT_REV_UP.get(), SoundSource.PLAYERS, 0.25F + 0.5F * now, 0.8F + 0.9F * now, false);
			}

			if (AnimUtil.justFinished(this.getPoints(), RiftPearlItem.USE_DURATION - RiftPearlItem.SHUFFLE_OFFSET - 2)) {
				this.level.playLocalSound(thisX, thisY, thisZ,
						ModSounds.RIFT_CLOSE.get(), SoundSource.PLAYERS, 1.0F, this.level.random.nextFloat() * 0.5F + 0.8F, false);
			}

			double range = RiftConfig.getRange();
			int count = (int) (range / 3.0D) + 1;

			for (int i = 0; i < count; i++) {
				double maxX = this.getRandomX(0.1D) + range;
				double maxY = this.getRandomY(0.1D) + range + 0.25D;
				double maxZ = this.getRandomZ(0.1D) + range;
				double minX = this.getRandomX(0.1D) - range;
				double minY = this.getRandomY(0.1D) - range - 0.25D;
				double minZ = this.getRandomZ(0.1D) - range;

				addRandomStationaryParticle(AnimUtil.random(this.random, -range, range) + thisX, maxY, maxZ);
				addRandomStationaryParticle(AnimUtil.random(this.random, -range, range) + thisX, maxY, minZ);
				addRandomStationaryParticle(AnimUtil.random(this.random, -range, range) + thisX, minY, maxZ);
				addRandomStationaryParticle(AnimUtil.random(this.random, -range, range) + thisX, minY, minZ);

				addRandomStationaryParticle(maxX, AnimUtil.random(this.random, -range, range) + thisY, maxZ);
				addRandomStationaryParticle(maxX, AnimUtil.random(this.random, -range, range) + thisY, minZ);
				addRandomStationaryParticle(minX, AnimUtil.random(this.random, -range, range) + thisY, maxZ);
				addRandomStationaryParticle(minX, AnimUtil.random(this.random, -range, range) + thisY, minZ);

				addRandomStationaryParticle(maxX, maxY, AnimUtil.random(this.random, -range, range) + thisZ);
				addRandomStationaryParticle(maxX, minY, AnimUtil.random(this.random, -range, range) + thisZ);
				addRandomStationaryParticle(minX, maxY, AnimUtil.random(this.random, -range, range) + thisZ);
				addRandomStationaryParticle(minX, minY, AnimUtil.random(this.random, -range, range) + thisZ);
			}
			addRandomStationaryParticle(thisX - 0.5D + this.random.nextDouble(), thisY + 0.25D, thisZ - 0.5D + this.random.nextDouble());

			List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(range - 0.25D).move(0, 0.2D, 0), Entity::isAlive);
			for (Entity entity : nearbyEntities) {
				if (!(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrame) && this.random.nextInt(USE_DURATION) <= points + USE_DURATION / 3) {
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
						ServerPlayer player = lastTo != null ? server.getPlayerList().getPlayer(lastTo) : null;
						boolean worked = false;

						List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(RiftConfig.getRange() - 0.25D).move(0, 0.2D, 0), Entity::isAlive);
						for (Entity entity : nearbyEntities) {
							if (!entity.getUUID().equals(lastTo) && !(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrame)) {
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
									server.tell(new TickTask(server.getTickCount(), () -> this.sendToSpawn(this, server, item, lastTo)));
									worked = true;
								}
								this.kill();
							}
						}

						if (player != null) {
							if (nearbyEntities.contains(player)) {
								server.tell(new TickTask(server.getTickCount(), () -> this.sendToSpawn(this, server, player, player)));
								worked = true;
							}
							server.tell(new TickTask(server.getTickCount(), () -> validateSpawn(server, player, nearbyEntities.isEmpty())));
						}

						this.entityData.set(DATA_WARP_POINTS, 0);
						if (worked) {
							this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundSource.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
						}
					}
				} else this.addPoints(1);
			} else if (points > 0) {
				if (this.addPoints(-Math.min(points, 2)) == 0) {
					this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_CLOSE.get(), SoundSource.BLOCKS, 0.3F, this.random.nextFloat() * 0.4F + 0.4F);
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
		SimpleParticleType particleType = ModParticles.RIFT_SMOKE.get();
		if (this.random.nextInt(USE_DURATION) < this.entityData.get(DATA_WARP_POINTS))
			this.level.addParticle(particleType, x, y, z, 0, 0, 0);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (!this.isRevving() && player instanceof ServerPlayer serverPlayer) {
			int price = this.getPrice(
					serverPlayer.blockPosition(),
					this,
					serverPlayer.getRespawnPosition() != null ? serverPlayer.getRespawnPosition() : BlockPos.ZERO
			);

			if (price > -1) {
				if (player.totalExperience >= price || player.getAbilities().instabuild) {
					if (!player.getAbilities().instabuild && price > 0) player.giveExperienceLevels(-price);
				} else return InteractionResult.FAIL;
			}

			this.startRevving(serverPlayer);
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof RiftPearlItem) this.setItem(stack);
			else this.setItem(new ItemStack(ModItems.RIFT_PEARL.get()));

			this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_OPEN.get(), SoundSource.BLOCKS, 0.8F, this.random.nextFloat() * 0.4F + 0.4F);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	public void validateSpawn(MinecraftServer server, ServerPlayer serverPlayer, boolean check) {
		BlockPos spawnPoint = serverPlayer.getRespawnPosition();
		float viewAngle = serverPlayer.getRespawnAngle();
		ServerLevel serverLevel = server.getLevel(serverPlayer.getRespawnDimension());

		if (serverLevel != null && spawnPoint != null) {
			Player.findRespawnPositionAndUseSpawnBlock(serverLevel, spawnPoint, viewAngle, false, check);
			if (serverLevel.getBlockState(spawnPoint).is(Blocks.RESPAWN_ANCHOR) && !check) {
				server.tell(new TickTask(server.getTickCount(), () ->
						serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), 1.0F, 1.0F))));
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("tick_count", this.tickCount);
		tag.putInt("warp_points", this.getEntityData().get(DATA_WARP_POINTS));
		tag.put("item", this.getItem().save(new CompoundTag()));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.tickCount = tag.getInt("tick_count");
		this.getEntityData().set(DATA_WARP_POINTS, tag.getInt("warp_points"));
		this.setItem(ItemStack.of(tag.getCompound("Item")));
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.getEntity() instanceof Player && !source.isProjectile() && !source.isMagic()) {
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
	public void onAddedToWorld() {
		this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_SPAWN.get(), SoundSource.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.6F);
		ItemStack stack = this.getItem();
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(RiftPearlItem.POWERED)) {
			RiftPearlItem.empowerPearl(stack, this.level.getGameTime());
			this.setItem(stack);
		}
		super.onAddedToWorld();
	}

	@Override
	public void kill() {
		this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_EXPIRE.get(), SoundSource.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.4F);
		super.kill();
	}

	@Override
	public Packet<?> getAddEntityPacket() {
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
		return Optional.of(NbtUtils.readBlockPos(stack.getOrCreateTag().getCompound(RiftPearlItem.POSITION)));
	}

	@Override
	public Optional<ResourceKey<Level>> savedWorld(BossRiftEntity rift) {
		ItemStack stack = rift.getItem();
		if (!stack.hasTag() || !stack.getOrCreateTag().contains(RiftPearlItem.DIMENSION)) return Optional.empty();
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get(RiftPearlItem.DIMENSION)).result();
	}
}