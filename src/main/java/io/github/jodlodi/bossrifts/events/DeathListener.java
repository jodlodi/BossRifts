package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class DeathListener {
    @SubscribeEvent
    @ParametersAreNonnullByDefault
    public static void livingEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() && Reg.RIFT_BOSSES.contains(entity.getType())) {

            List<Entity> list = entity.level.getEntities(entity, entity.getBoundingBox().inflate(32), Entity::isAlive);
            if (list.stream().anyMatch(o -> o instanceof LivingEntity living && Reg.RIFT_BOSSES.contains(living.getType()) && !living.isDeadOrDying())) return;

            BossRiftEntity bossRift = Reg.BOSS_RIFT.get().create(entity.level);
            if (bossRift != null) {
                BlockPos pos = entity.blockPosition().above();
                if (entity.level.getBlockState(pos).isCollisionShapeFullBlock(entity.level, pos)) {
                    entity.level.removeBlock(pos, false);
                }
                bossRift.moveTo(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
                entity.level.addFreshEntity(bossRift);
            }
        }
    }
}