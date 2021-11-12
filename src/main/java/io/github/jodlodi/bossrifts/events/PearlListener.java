package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class PearlListener {
    @SubscribeEvent
    public static void playerEnderPearl(EntityTeleportEvent.EnderPearl event) {
        ServerPlayerEntity serverPlayer = event.getPlayer();
        EnderPearlEntity pearl = event.getPearlEntity();
        MinecraftServer server = pearl.getServer();
        List<Entity> nearbyRifts = pearl.level.getEntitiesOfClass(BossRiftEntity.class, pearl.getBoundingBox().inflate(1.6D), EntityPredicates.ENTITY_STILL_ALIVE);
        if (!nearbyRifts.isEmpty()) {
            BossRiftEntity rift = (BossRiftEntity)nearbyRifts.get(0);
            if (server != null && rift.validateSpawn(server, serverPlayer,false)) {
                event.setCanceled(true);
                serverPlayer.fallDistance = 0.0F;
                serverPlayer.hurt(DamageSource.FALL, event.getAttackDamage());
                server.tell(new TickDelayedTask(server.getTickCount(), () -> rift.sendToSpawn(server, serverPlayer.getEntity(), serverPlayer)));
            }
        }
    }
}
