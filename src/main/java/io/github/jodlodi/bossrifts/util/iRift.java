package io.github.jodlodi.bossrifts.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface iRift<T> {
	int USE_DURATION = 300;
	int EMPOWER_DURATION = 500;
	int TRANSITION_TIME = 50;
	int SHUFFLE_OFFSET = 5;
	int COMPRESS_OFFSET = 10;

	long getSeed(T t);

	Optional<BlockPos> savedPos(T t);

	Optional<RegistryKey<World>> savedWorld(T t);

	default void sendToSpawn(T t, MinecraftServer server, Entity entity, ServerPlayerEntity serverPlayer) {
		ServerWorld spawnWorld = this.savedWorld(t).map(server::getLevel).orElse(server.getLevel(serverPlayer.getRespawnDimension()));
		if (spawnWorld == null) spawnWorld = Objects.requireNonNull(server.getLevel(World.OVERWORLD));

		Vector3d vector3d = this.getSpawnPos(t, spawnWorld, serverPlayer);

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

	default Vector3d getSpawnPos(T t, ServerWorld spawnWorld, ServerPlayerEntity serverPlayer) {
		BlockPos spawnPoint = this.savedPos(t).orElse(serverPlayer.getRespawnPosition());
		float viewAngle = serverPlayer.getRespawnAngle();

		if (spawnPoint != null) {
			Optional<Vector3d> vec3 = PlayerEntity.findRespawnPositionAndUseSpawnBlock(spawnWorld, spawnPoint, viewAngle, false, true);
			if (vec3.isPresent()) return vec3.get();
		}

		return new Vector3d(spawnWorld.getSharedSpawnPos().getX() + 0.5D, spawnWorld.getSharedSpawnPos().getY(), spawnWorld.getSharedSpawnPos().getZ() + 0.5D);
	}
}
