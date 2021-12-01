package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class PearlListener {
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
                serverPlayer.hurt(DamageSource.FALL, event.getAttackDamage());
                server.tell(new net.minecraft.server.TickTask(server.getTickCount(), () -> rift.sendToSpawn(server, serverPlayer, serverPlayer)));
                server.tell(new net.minecraft.server.TickTask(server.getTickCount(), () -> rift.validateSpawn(server, serverPlayer,false)));
            }
        }
    }
}