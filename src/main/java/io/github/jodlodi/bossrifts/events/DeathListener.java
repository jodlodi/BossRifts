package io.github.jodlodi.bossrifts.events;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.rift.BossRiftEntity;
import io.github.jodlodi.bossrifts.registry.Reg;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
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
        if (entity.getType().is(Reg.RIFT_BOSSES) && !entity.level.isClientSide()) {

            List<Entity> list = entity.level.getEntities(entity, entity.getBoundingBox().inflate(32), EntityPredicates.ENTITY_STILL_ALIVE);
            if (list.stream().anyMatch(o -> o instanceof LivingEntity && o.getType().is(Reg.RIFT_BOSSES) && !((LivingEntity)o).isDeadOrDying())) return;

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
