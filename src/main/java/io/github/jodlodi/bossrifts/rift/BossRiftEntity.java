package io.github.jodlodi.bossrifts.rift;

import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraftforge.event.ForgeEventFactory.onEntityTeleportCommand;
import static net.minecraftforge.event.entity.EntityTeleportEvent.TeleportCommand;

@SuppressWarnings("ALL")
public class BossRiftEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_WARP_POINTS = SynchedEntityData.defineId(BossRiftEntity.class, EntityDataSerializers.INT);
    public int warpSpan = 160;
    public float time;
    public float revSpeed;
    private int lastRev;
    public boolean warpYesNoMaybe;
    private ServerPlayer lastToTouch;

    public BossRiftEntity(EntityType<? extends BossRiftEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.time = 0;
        this.revSpeed = 0;
        this.lastRev = 0;
        this.warpYesNoMaybe = false;
    }

    public BossRiftEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(level);
    }

    public BossRiftEntity(Level level) {
        super(Reg.BOSS_RIFT.get(), level);
        this.blocksBuilding = true;
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_WARP_POINTS, 0);
    }

    public void tick() {
        double multi = 2.25D;
        ++this.time;
        float revUp = (float)Math.pow((double)getPoints() / (double)this.warpSpan, 2D);
        this.revSpeed += (revUp / 10F) * (float)getPoints();

        if (this.level.isClientSide) {
            float pause = (3F / revUp);
            if (getPoints() > 0 && Math.abs(this.lastRev - this.time) >= pause) {
                this.level.playLocalSound(this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_REV_UP.get(), SoundSource.BLOCKS, 0.1F + revUp / 2, revUp + (float)getPoints() / 120F - 1.5F, false);
                this.lastRev = (int)this.time;
            }
            double maxX = this.getRandomX(0.1D) + 2.5D;
            double maxY = this.getRandomY(0.1D) + 2.75D;
            double maxZ = this.getRandomZ(0.1D) + 2.5D;
            double minX = this.getRandomX(0.1D) - 2.5D;
            double minY = this.getRandomY(0.1D) - 2.25D;
            double minZ = this.getRandomZ(0.1D) - 2.5D;

            addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + this.getX(), maxY, maxZ);
            addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + this.getX(), maxY, minZ);
            addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + this.getX(), minY, maxZ);
            addRandomStationaryParticle((double) (this.random.nextInt(49) - 25) / 10 + this.getX(), minY, minZ);
            addRandomStationaryParticle(maxX, (double) (this.random.nextInt(49) - 25) / 10 + this.getY(), maxZ);
            addRandomStationaryParticle(maxX, (double) (this.random.nextInt(49) - 25) / 10 + this.getY(), minZ);
            addRandomStationaryParticle(minX, (double) (this.random.nextInt(49) - 25) / 10 + this.getY(), maxZ);
            addRandomStationaryParticle(minX, (double) (this.random.nextInt(49) - 25) / 10 + this.getY(), minZ);
            addRandomStationaryParticle(maxX, maxY, (double) (this.random.nextInt(49) - 25) / 10 + this.getZ());
            addRandomStationaryParticle(maxX, minY, (double) (this.random.nextInt(49) - 25) / 10 + this.getZ());
            addRandomStationaryParticle(minX, maxY, (double) (this.random.nextInt(49) - 25) / 10 + this.getZ());
            addRandomStationaryParticle(minX, minY, (double) (this.random.nextInt(49) - 25) / 10 + this.getZ());

            double d0 = this.getX() - 0.5D + this.random.nextDouble();
            double d1 = this.getY() + 0.25D;
            double d2 = this.getZ() - 0.5D + this.random.nextDouble();
            addRandomStationaryParticle(d0, d1, d2);


            List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(multi).move(0, 0.25D, 0), Entity::isAlive);
            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrame) && this.random.nextInt(this.warpSpan) <= getPoints() + this.warpSpan / 3) {
                    double itemFix = 0D;
                    if (entity instanceof ItemEntity) itemFix = 0.25D;
                    this.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D - itemFix), entity.getRandomY() + itemFix, entity.getRandomZ(0.5D - itemFix), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D - itemFix);
                }
            }
        }

        MinecraftServer server = this.getServer();
        if (warpYesNoMaybe) {
            if (getPoints() >= warpSpan) {
                if (server != null && !this.level.isClientSide) {
                    List<Entity> nearbyEntities = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(multi).move(0, 0.25D, 0), Entity::isAlive);
                    for (Entity entity : nearbyEntities) {
                        if (entity.getUUID() != this.lastToTouch.getUUID() && !(entity instanceof BossRiftEntity) && !(entity instanceof ItemFrame)) {
                            this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_WARP.get(), SoundSource.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
                            sendToSpawn(server, entity, this.lastToTouch);
                        }
                    }
                    if (RiftConfig.reusableState) this.remove(Entity.RemovalReason.KILLED);
                    else this.warpYesNoMaybe = false;
                    if (nearbyEntities.contains(this.lastToTouch)) {
                        this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_WARP.get(), SoundSource.BLOCKS, 1F, this.random.nextFloat() * 0.4F + 0.5F);
                        server.tell(new TickTask(server.getTickCount(), () -> sendToSpawn(server, this.lastToTouch, this.lastToTouch)));
                    }
                    server.tell(new TickTask(server.getTickCount(), () -> validateSpawn(server, this.lastToTouch, nearbyEntities.isEmpty())));
                }
            } else addPoints(1);
        } else if (getPoints() > 0) {
            addPoints(-1);
            if (getPoints() > 0) addPoints(-1);
            if (getPoints() == 0) {
                this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_CLOSE.get(), SoundSource.BLOCKS, 0.3F, this.random.nextFloat() * 0.4F + 0.4F);
            }
        } else if (!this.level.isClientSide && RiftConfig.expireState && this.time >= RiftConfig.expireSpan) this.kill();
    }

    public double getRandomY(double size) {
        return this.getY((2.0D * this.random.nextDouble() - 1.0D) * size);
    }

    private void addRandomStationaryParticle(double x, double y, double z) {
        if (this.random.nextInt(this.warpSpan) < getPoints()) this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        MinecraftServer server = this.getServer();
        if (server != null && !warpYesNoMaybe) {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
            if (serverPlayer != null) {
                warpYesNoMaybe = true;
                this.lastToTouch = serverPlayer;
                this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_OPEN.get(), SoundSource.BLOCKS, 0.8F, this.random.nextFloat() * 0.4F + 0.4F);
                return InteractionResult.SUCCESS;
            }
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

    public void sendToSpawn(MinecraftServer server, Entity entity, ServerPlayer serverPlayer) {
        BlockPos spawnPoint = serverPlayer.getRespawnPosition();
        float viewAngle = serverPlayer.getRespawnAngle();
        ServerLevel spawnLevel = Objects.requireNonNull(server.getLevel(serverPlayer.getRespawnDimension()));

        Optional<Vec3> optional;
        if (spawnPoint != null) {
            optional = Player.findRespawnPositionAndUseSpawnBlock(spawnLevel, spawnPoint, viewAngle, false, true);
        } else optional = Optional.empty();

        if (!optional.isPresent()) spawnLevel = Objects.requireNonNull(server.getLevel(Level.OVERWORLD));
        Vec3 worldSpawn = new Vec3(spawnLevel.getSharedSpawnPos().getX() + 0.5D, spawnLevel.getSharedSpawnPos().getY(), spawnLevel.getSharedSpawnPos().getZ() + 0.5D);

        Vec3 vec3 = optional.orElse(worldSpawn);

        TeleportCommand event = onEntityTeleportCommand(entity, vec3.x, vec3.y, vec3.z);
        if (event.isCanceled()) return;

        double dx = event.getTargetX();
        double dy = event.getTargetY();
        double dz = event.getTargetZ();

        if (entity instanceof ServerPlayer) {
            ChunkPos chunkpos = new ChunkPos(new BlockPos(dx, dy, dz));
            spawnLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());
            entity.stopRiding();

            if (((ServerPlayer)entity).isSleeping()) ((ServerPlayer)entity).stopSleepInBed(true, true);
            if (spawnLevel == entity.level) {
                ((ServerPlayer)entity).connection.teleport(dx, dy, dz, entity.getYRot(), entity.getXRot());
            } else {
                ((ServerPlayer)entity).teleportTo(spawnLevel, dx, dy, dz, entity.getYRot(), entity.getXRot());
            }
        } else {
            float f1 = Mth.wrapDegrees(entity.getYRot());
            float f = Mth.wrapDegrees(entity.getXRot());


            f = Mth.clamp(f, -90.0F, 90.0F);
            if (spawnLevel == entity.level) {
                entity.moveTo(dx, dy, dz, f1, f);
                entity.setYHeadRot(f1);
            } else {
                entity.unRide();
                Entity newEntity = entity.getType().create(spawnLevel);
                if (newEntity != null) {
                    newEntity.restoreFrom(entity);
                    entity.remove(RemovalReason.CHANGED_DIMENSION);
                    newEntity.moveTo(dx, dy, dz, f1, f);
                    newEntity.setYHeadRot(f1);
                    spawnLevel.addFreshEntity(newEntity);
                }
            }
        }
        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            entity.setOnGround(true);
        }
        if (entity instanceof AmbientCreature) ((AmbientCreature) entity).getNavigation().stop();
        if (entity instanceof ItemEntity) ((ItemEntity) entity).setExtendedLifetime();
    }

    public boolean hurt(DamageSource source, float damage) {
        if (source.getEntity() instanceof Player && !source.isProjectile() && !source.isMagic()) {
            this.warpYesNoMaybe = false;
            this.markHurt();
        }
        return false;
    }

    public void addPoints(int newPoints) {
        this.getEntityData().set(DATA_WARP_POINTS, getPoints() + newPoints);
    }

    public int getPoints() {
        return this.getEntityData().get(DATA_WARP_POINTS);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPickable() {
        return true;
    }

    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void onAddedToWorld() {
        this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_SPAWN.get(), SoundSource.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.6F);
        super.onAddedToWorld();
    }

    public void kill() {
        this.level.playSound(null, this.getX(), this.getY() + 0.25D, this.getZ(), Reg.RIFT_EXPIRE.get(), SoundSource.BLOCKS, 1.0F, this.random.nextFloat() * 0.4F + 0.4F);
        super.kill();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}