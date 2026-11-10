package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {

	public ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "onHitEntity", at = @At(value = "HEAD"), cancellable = true)
	private void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
		if (entityHitResult.getEntity() instanceof BossRiftEntity rift && rift.level() instanceof ServerLevel serverLevel && this.getOwner() instanceof ServerPlayer player) {
			ci.cancel();
			player.fallDistance = 0.0F;
			player.hurt(player.damageSources().fall(), 5.0F);

			MinecraftServer server = serverLevel.getServer();
			server.tell(new TickTask(server.getTickCount(), () -> rift.sendToSpawn(rift, server, player, player)));
			server.tell(new TickTask(server.getTickCount(), () -> rift.validateSpawn(player, false)));
		}
	}
}
