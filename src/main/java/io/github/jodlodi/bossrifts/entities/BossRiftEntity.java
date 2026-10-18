package io.github.jodlodi.bossrifts.entities;

import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModEntities;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BossRiftEntity extends Entity implements iRift<BossRiftEntity> {
	private static final DataParameter<Integer> DATA_WARP_POINTS = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.INT);
	private static final DataParameter<BlockPos> CREATION = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.BLOCK_POS);

	private int clientWarp = 0;
	private int clientWarp0 = 0;

	@Nullable
	private BlockPos destination = null;
	public boolean warpYesNoMaybe;
	private ServerPlayerEntity lastToTouch;

	public BossRiftEntity(EntityType<? extends BossRiftEntity> entityType, World world) {
		super(entityType, world);
		this.blocksBuilding = true;
		this.warpYesNoMaybe = false;
	}

	public BossRiftEntity(FMLPlayMessages.SpawnEntity ignoredSpawnEntity, World world) {
		this(world);
	}

	public BossRiftEntity(World world) {
		super(ModEntities.BOSS_RIFT.get(), world);
		this.blocksBuilding = true;
	}

	@Nullable
	public BlockPos getDestination() {
		return destination;
	}

	public void setDestination(@Nullable BlockPos destination) {
		this.destination = destination;
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(DATA_WARP_POINTS, 0);
		this.getEntityData().define(CREATION, BlockPos.of(-1L));
	}

	@Override
	public void tick() {
		this.setCreationIfEmpty(this.level.getGameTime());
		double multi = 2.25D;
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

			double maxX = this.getRandomX(0.1D) + 2.5D;
			double maxY = this.getRandomY(0.1D) + 2.75D;
			double maxZ = this.getRandomZ(0.1D) + 2.5D;
			double minX = this.getRandomX(0.1D) - 2.5D;
			double minY = this.getRandomY(0.1D) - 2.25D;
			double minZ = this.getRandomZ(0.1D) - 2.5D;

			addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + thisX, maxY, maxZ);
			addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + thisX, maxY, minZ);
			addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + thisX, minY, maxZ);
			addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + thisX, minY, minZ);

			addRandomStationaryParticle(maxX, (double) (this.random.nextInt(49) - 25) / 10 + thisY, maxZ);
			addRandomStationaryParticle(maxX, (double) (this.random.nextInt(49) - 25) / 10 + thisY, minZ);
			addRandomStationaryParticle(minX, (double) (this.random.nextInt(49) - 25) / 10 + thisY, maxZ);
			addRandomStationaryParticle(minX, (double) (this.random.nextInt(49) - 25) / 10 + thisY, minZ);

			addRandomStationaryParticle(maxX, maxY, (double) (this.random.nextInt(49) - 25) / 10 + thisZ);
			addRandomStationaryParticle(maxX, minY, (double) (this.random.nextInt(49) - 25) / 10 + thisZ);
			addRandomStationaryParticle(minX, maxY, (double) (this.random.nextInt(49) - 25) / 10 + thisZ);
			addRandomStationaryParticle(minX, minY, (double) (this.random.nextInt(49) - 25) / 10 + thisZ);

			double d0 = thisX - 0.5D + this.random.nextDouble();
			double d1 = thisY + 0.25D;
			double d2 = thisZ - 0.5D + this.random.nextDouble();
			addRandomStationaryParticle(d0, d1, d2);

			List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(multi).move(0, 0.2D, 0), EntityPredicates.ENTITY_STILL_ALIVE);
			for (Entity entity : nearbyEntities) {
				if (!(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity) && this.random.nextInt(USE_DURATION) <= points + USE_DURATION / 3) {
					double itemFix = 0D;
					if (entity instanceof ItemEntity) itemFix = 0.25D;
					this.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D - itemFix), entity.getRandomY() + itemFix, entity.getRandomZ(0.5D - itemFix), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix);
				}
			}
		} else {
			if (this.warpYesNoMaybe) {
				if (points >= USE_DURATION) {
					MinecraftServer server = this.getServer();
					if (server != null) {
						List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(multi).move(0, 0.2D, 0), EntityPredicates.ENTITY_STILL_ALIVE);
						for (Entity entity : nearbyEntities) {
							if (entity.getUUID() != this.lastToTouch.getUUID() && !(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity)) {
								this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundCategory.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
								this.sendToSpawn(this, server, entity, this.lastToTouch);
							}
						}
						if (RiftConfig.isReusable()) this.remove();
						else this.warpYesNoMaybe = false;
						if (nearbyEntities.contains(this.lastToTouch.getEntity())) {
							this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundCategory.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
							server.tell(new TickDelayedTask(server.getTickCount(), () -> this.sendToSpawn(this, server, this.lastToTouch, this.lastToTouch)));
						}
						server.tell(new TickDelayedTask(server.getTickCount(), () -> validateSpawn(server, this.lastToTouch, nearbyEntities.isEmpty())));
						this.entityData.set(DATA_WARP_POINTS, 0);
					}
				} else this.addPoints(1);
			} else if (points > 0) {
				if (this.addPoints(-Math.min(points, 2)) == 0) {
					this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_CLOSE.get(), SoundCategory.BLOCKS, 0.3F, this.random.nextFloat() * 0.4F + 0.4F);
				}
			} else if (RiftConfig.canExpire() && this.tickCount >= RiftConfig.getExpireSpan()) {
				this.kill();
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
		MinecraftServer server = this.getServer();
		if (server != null && !this.warpYesNoMaybe) {
			ServerPlayerEntity serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
			if (serverPlayer != null) {
				this.warpYesNoMaybe = true;
				this.lastToTouch = server.getPlayerList().getPlayer(player.getUUID());
				this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), ModSounds.RIFT_OPEN.get(), SoundCategory.BLOCKS, 0.8F, this.random.nextFloat() * 0.4F + 0.4F);
				return ActionResultType.SUCCESS;
			}
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
		tag.putLong("creation", this.getCreation());
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT tag) {
		this.tickCount = tag.getInt("tick_count");
		this.getEntityData().set(DATA_WARP_POINTS, tag.getInt("warp_points"));
		this.setCreation(tag.getLong("creation"));
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.getEntity() instanceof PlayerEntity && !source.isProjectile() && !source.isMagic()) {
			this.warpYesNoMaybe = false;
			this.markHurt();
		}
		return false;
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

	public void setCreation(long creation) {
		this.entityData.set(CREATION, BlockPos.of(creation));
	}

	public long getCreation() {
		return this.entityData.get(CREATION).asLong();
	}

	public void setCreationIfEmpty(long creation) {
		long current = this.getCreation();
		if (current == -1L) this.setCreation(creation);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public void onAddedToWorld() {
		this.setCreationIfEmpty(this.level.getGameTime());
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
		return this.getUUID().getMostSignificantBits();
	}

	@Override
	public Optional<BlockPos> savedPos(BossRiftEntity rift) {
		return Optional.empty();
	}

	@Override
	public Optional<RegistryKey<World>> savedWorld(BossRiftEntity rift) {
		return Optional.empty();
	}
}