package io.github.jodlodi.bossrifts.entities;

import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.TeleportCommand;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BossRiftEntity extends Entity {
	private static final DataParameter<Integer> DATA_WARP_POINTS = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> DATA_WARP_POINTS_0 = EntityDataManager.defineId(BossRiftEntity.class, DataSerializers.INT);
	public static final int WARP_SPAN = 160;
	private int lastRev;
	private float revSpeed;
	public float rot = 0.0F;
	public boolean warpYesNoMaybe;
	private ServerPlayerEntity lastToTouch;

	public BossRiftEntity(EntityType<? extends BossRiftEntity> entityType, World world) {
		super(entityType, world);
		this.blocksBuilding = true;
		this.lastRev = 0;
		this.warpYesNoMaybe = false;
	}

	public BossRiftEntity(FMLPlayMessages.SpawnEntity ignoredSpawnEntity, World world) {
		this(world);
	}

	public BossRiftEntity(World world) {
		super(ModEntities.BOSS_RIFT.get(), world);
		this.blocksBuilding = true;
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(DATA_WARP_POINTS, 0);
		this.getEntityData().define(DATA_WARP_POINTS_0, 0);
	}

	@Override
	public void tick() {
		double multi = 2.25D;
		int points = this.getPoints();
		float revUp = (float) Math.pow((double) points / (double) WARP_SPAN, 2D);

		double thisX = this.getX();
		double thisY = this.getY();
		double thisZ = this.getZ();

		if (this.level.isClientSide) {
			this.revSpeed += (revUp / 10F) * (float) getPoints() + 0.5F;
			this.rot = MathHelper.wrapDegrees(this.rot + this.revSpeed);
			this.yRot = this.rot;
			this.revSpeed *= 0.25F;

			float pause = (3F / revUp);
			if (points > 0 && Math.abs(this.lastRev - this.tickCount) >= pause) {
				this.level.playLocalSound(thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_REV_UP.get(), SoundCategory.BLOCKS, 0.1F + revUp / 2, revUp + (float) points / 120F - 1.5F, false);
				this.lastRev = this.tickCount;
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
				if (!(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity) && this.random.nextInt(WARP_SPAN) <= points + WARP_SPAN / 3) {
					double itemFix = 0D;
					if (entity instanceof ItemEntity) itemFix = 0.25D;
					this.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D - itemFix), entity.getRandomY() + itemFix, entity.getRandomZ(0.5D - itemFix), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix);
				}
			}
		} else {
			this.getEntityData().set(DATA_WARP_POINTS_0, points);

			if (this.warpYesNoMaybe) {
				if (points >= WARP_SPAN) {
					MinecraftServer server = this.getServer();
					if (server != null) {
						List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(multi).move(0, 0.2D, 0), EntityPredicates.ENTITY_STILL_ALIVE);
						for (Entity entity : nearbyEntities) {
							if (entity.getUUID() != this.lastToTouch.getUUID() && !(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrameEntity)) {
								this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundCategory.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
								sendToSpawn(server, entity, this.lastToTouch);
							}
						}
						if (RiftConfig.reusableState) this.remove();
						else this.warpYesNoMaybe = false;
						if (nearbyEntities.contains(this.lastToTouch.getEntity())) {
							this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_WARP.get(), SoundCategory.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
							server.tell(new TickDelayedTask(server.getTickCount(), () -> sendToSpawn(server, this.lastToTouch, this.lastToTouch)));
						}
						server.tell(new TickDelayedTask(server.getTickCount(), () -> validateSpawn(server, this.lastToTouch, nearbyEntities.isEmpty())));
					}
				} else this.addPoints(1);
			} else if (points > 0) {
				if (this.addPoints(-Math.min(points, 2)) == 0) {
					this.level.playSound(null, thisX, thisY + 0.25D, thisZ, ModSounds.RIFT_CLOSE.get(), SoundCategory.BLOCKS, 0.3F, this.random.nextFloat() * 0.4F + 0.4F);
				}
			} else if (RiftConfig.expireState && this.tickCount >= RiftConfig.expireSpan) {
				this.kill();
			}
		}
	}

	public double getRandomY(double size) {
		return this.getY((2.0D * this.random.nextDouble() - 1.0D) * size);
	}

	private void addRandomStationaryParticle(double x, double y, double z) {
		BasicParticleType particleType = ParticleTypes.SMOKE;
		if (this.random.nextInt(WARP_SPAN) < this.getPoints()) this.level.addParticle(particleType, x, y, z, 0, 0, 0);
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

	public static Vector3d getSpawnPos(MinecraftServer server, ServerPlayerEntity serverPlayer) {
		BlockPos spawnPoint = serverPlayer.getRespawnPosition();
		float viewAngle = serverPlayer.getRespawnAngle();
		ServerWorld spawnWorld = server.getLevel(serverPlayer.getRespawnDimension());

		if (spawnPoint != null && spawnWorld != null) {
			Optional<Vector3d> vec3 = PlayerEntity.findRespawnPositionAndUseSpawnBlock(spawnWorld, spawnPoint, viewAngle, false, true);
			if (vec3.isPresent()) return vec3.get();
		}

		spawnWorld = Objects.requireNonNull(server.getLevel(World.OVERWORLD));
		return new Vector3d(spawnWorld.getSharedSpawnPos().getX() + 0.5D, spawnWorld.getSharedSpawnPos().getY(), spawnWorld.getSharedSpawnPos().getZ() + 0.5D);
	}

	public void sendToSpawn(MinecraftServer server, Entity entity, ServerPlayerEntity serverPlayer) {
		ServerWorld spawnWorld = server.getLevel(serverPlayer.getRespawnDimension());
		if (spawnWorld == null) spawnWorld = Objects.requireNonNull(server.getLevel(World.OVERWORLD));

		Vector3d vector3d = getSpawnPos(server, serverPlayer);

		TeleportCommand event = ForgeEventFactory.onEntityTeleportCommand(entity, vector3d.x, vector3d.y, vector3d.z);
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
	protected void addAdditionalSaveData(CompoundNBT tag) {
		tag.putInt("tick_count", this.tickCount);
		tag.putInt("warp_points", this.getEntityData().get(DATA_WARP_POINTS));
		tag.putInt("warp_points_0", this.getEntityData().get(DATA_WARP_POINTS_0));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT tag) {
		this.tickCount = tag.getInt("tick_count");
		this.getEntityData().set(DATA_WARP_POINTS, tag.getInt("warp_points"));
		this.getEntityData().set(DATA_WARP_POINTS_0, tag.getInt("warp_points_0"));
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
		int newPoints = this.getPoints() + add;
		this.setPoints(newPoints);
		return newPoints;
	}

	public void setPoints(int points) {
		this.getEntityData().set(DATA_WARP_POINTS, points);
	}

	public int getPoints() {
		return this.getEntityData().get(DATA_WARP_POINTS);
	}

	public float getLerpedPoints(float partialTick) {
		return MathHelper.lerp(partialTick, this.getEntityData().get(DATA_WARP_POINTS_0), this.getEntityData().get(DATA_WARP_POINTS));
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
}