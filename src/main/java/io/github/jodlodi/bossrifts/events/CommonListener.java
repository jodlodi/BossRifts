package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.RiftConfig;
import io.github.jodlodi.bossrifts.registry.Reg;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class CommonListener {

    @SubscribeEvent
    public static void livingEntityDeath(LivingDeathEvent event) {
        LivingEntity dyingEntity = event.getEntity();
        if (!dyingEntity.level.isClientSide() && dyingEntity.getType().is(Tags.EntityTypes.BOSSES) && !dyingEntity.getType().is(Reg.BOSS_EXCEPTION)) {
            if (!dyingEntity.level.getEntities(dyingEntity, dyingEntity.getBoundingBox().inflate(32),
                    entity -> entity.getType().is(Tags.EntityTypes.BOSSES) && entity instanceof LivingEntity living && !living.isDeadOrDying()).isEmpty()) return;

            BossRiftEntity bossRift = Reg.BOSS_RIFT.get().create(dyingEntity.level);
            if (bossRift != null) {
                BlockPos pos = dyingEntity.blockPosition().above();
                if (dyingEntity.level.getBlockState(pos).isCollisionShapeFullBlock(dyingEntity.level, pos)) {
                    dyingEntity.level.removeBlock(pos, false);
                }
                bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
                dyingEntity.level.addFreshEntity(bossRift);
            }
        }
    }

    @SubscribeEvent
    public static void playerEnderPearl(EntityTeleportEvent.EnderPearl event) {
        ServerPlayer serverPlayer = event.getPlayer();
        ThrownEnderpearl pearl = event.getPearlEntity();
        MinecraftServer server = pearl.getServer();
        List<BossRiftEntity> nearbyRifts = pearl.level.getEntitiesOfClass(BossRiftEntity.class, pearl.getBoundingBox().inflate(1.6D), Entity::isAlive);
        if (!nearbyRifts.isEmpty()) {
            BossRiftEntity rift = nearbyRifts.get(0);
            if (server != null) {
                event.setCanceled(true);
                serverPlayer.fallDistance = 0.0F;
                serverPlayer.hurt(pearl.damageSources().fall(), event.getAttackDamage());
                server.tell(new net.minecraft.server.TickTask(server.getTickCount(), () -> rift.sendToSpawn(server, serverPlayer, serverPlayer)));
                server.tell(new net.minecraft.server.TickTask(server.getTickCount(), () -> rift.validateSpawn(server, serverPlayer,false)));
            }
        }
    }

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        RiftConfig.refresh();
    }
}