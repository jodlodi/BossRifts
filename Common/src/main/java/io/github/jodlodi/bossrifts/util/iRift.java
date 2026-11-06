package io.github.jodlodi.bossrifts.util;

import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface iRift<T> {
	int USE_DURATION = 300;
	int TRANSITION_TIME = 50;
	int SHUFFLE_OFFSET = 5;
	int COMPRESS_OFFSET = 10;

	long getSeed(T t);

	Optional<BlockPos> savedPos(T t);

	Optional<ResourceKey<Level>> savedWorld(T t);

	default void sendToSpawn(T t, MinecraftServer server, Entity entity, @Nullable UUID serverPlayer) {
		this.sendToSpawn(t, server, entity, serverPlayer == null ? null : server.getPlayerList().getPlayer(serverPlayer));
	}

	default void sendToSpawn(T t, MinecraftServer server, Entity entity, @Nullable ServerPlayer serverPlayer) {
		ServerLevel spawnWorld = serverPlayer != null ? this.savedWorld(t).map(server::getLevel).orElse(server.getLevel(serverPlayer.getRespawnDimension())) : null;
		if (spawnWorld == null) spawnWorld = Objects.requireNonNull(server.getLevel(Level.OVERWORLD));

		Vec3 vector3d = this.getSpawnPos(t, spawnWorld, serverPlayer);
		Vec3 mod = Services.EVENT.onTeleport(entity, vector3d);
		if (mod == null) return;

		if (entity instanceof ServerPlayer player) {
			ChunkPos chunkpos = new ChunkPos(new BlockPos(mod.x, mod.y, mod.z));
			spawnWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());
			entity.stopRiding();

			if (player.isSleeping()) player.stopSleepInBed(true, true);
			if (spawnWorld == entity.level) {
				player.connection.teleport(mod.x, mod.y, mod.z, entity.getYRot(), entity.getXRot());
			} else {
				player.teleportTo(spawnWorld, mod.x, mod.y, mod.z, entity.getYRot(), entity.getXRot());
			}
		} else {
			float f1 = Mth.wrapDegrees(entity.getYRot());
			float f = Mth.wrapDegrees(entity.getXRot());

			f = Mth.clamp(f, -90.0F, 90.0F);
			if (spawnWorld == entity.level) {
				entity.moveTo(mod.x, mod.y, mod.z, f1, f);
				entity.setYHeadRot(f1);
			} else {
				entity.unRide();
				Entity newEntity = entity.getType().create(spawnWorld);
				if (newEntity != null) {
					newEntity.restoreFrom(entity);
					entity.remove(Entity.RemovalReason.DISCARDED);
					newEntity.moveTo(mod.x, mod.y, mod.z, f1, f);
					newEntity.setYHeadRot(f1);
					spawnWorld.addFreshEntity(newEntity);
				}
			}
		}
		if (!(entity instanceof LivingEntity living) || !living.isFallFlying()) {
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}
		if (entity instanceof Mob mob) mob.getNavigation().stop();
		if (entity instanceof ItemEntity item) item.setExtendedLifetime();
	}

	default Vec3 getSpawnPos(T t, ServerLevel spawnWorld, @Nullable ServerPlayer serverPlayer) {
		BlockPos spawnPoint = serverPlayer != null ? this.savedPos(t).orElse(serverPlayer.getRespawnPosition()) : null;
		float viewAngle = serverPlayer != null ? serverPlayer.getRespawnAngle() : 0.0F;

		if (spawnPoint != null) {
			Optional<Vec3> vec3 = Player.findRespawnPositionAndUseSpawnBlock(spawnWorld, spawnPoint, viewAngle, false, true);
			if (vec3.isPresent()) return vec3.get();
		}

		return new Vec3(spawnWorld.getSharedSpawnPos().getX() + 0.5D, spawnWorld.getSharedSpawnPos().getY(), spawnWorld.getSharedSpawnPos().getZ() + 0.5D);
	}

	default int getPrice(BlockPos pos, T t, BlockPos or) {
		double cost = Services.CONFIG.getExpCost();
		if (Mth.equal(cost, 0.0D)) return 0;
		if (cost > 0.0D) {
			BlockPos spawn = this.savedPos(t).orElse(or);
			double dis = Math.sqrt(pos.distSqr(spawn)) / 16.0D;
			return Math.max((int) (cost * dis), 1);
		}
		return -1;
	}
}
